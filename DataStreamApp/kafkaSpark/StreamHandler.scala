import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.cassandra._

import com.datastax.oss.driver.api.core.uuid.Uuids 
import com.datastax.spark.connector._ 

case class Expense(userid: Int, timestamp: String, description: String, expensetype: String, count: Int, payment: Double)

object StreamHandler {
	def main(args: Array[String]) {
		val spark = SparkSession
			.builder
			.appName("Stream Handler")
			.config("spark.cassandra.connection.host", "localhost")
			.config("spark.cassandra.input.consistency.level", "ONE")
			.config("spark.cassandra.output.consistency.level", "ONE")
			.getOrCreate()

		import spark.implicits._

		val topicPattern = "user_\\d+"

		val inputDF = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092")
			.option("subscribePattern", topicPattern)
			.load()

		val expenseDF = inputDF
		.selectExpr("CAST(value AS STRING)")
		.as[String]
		.map { row =>
			val cleanedRow = row.replaceAll("[<>]", "").trim
			val fields = cleanedRow.split(",")
			Expense(
			fields(0).trim.toInt, 
			fields(1).trim,
			fields(2).trim,
			fields(3).trim,
			fields(4).trim.toInt,
			fields(5).trim.toDouble
			)
		}

		val makeUUID = udf(() => Uuids.timeBased().toString)

		val expenseWithID = expenseDF.withColumn("uuid", makeUUID())

		val query = expenseWithID
			.writeStream
			.trigger(Trigger.ProcessingTime("5 seconds"))
			.foreachBatch { (batchDF: DataFrame, batchID: Long) =>
				println(s"Writing to Cassandra $batchID")
				batchDF.write
					.cassandraFormat("expenses", "emp")
					.mode("append")
					.save()
			}
			.outputMode("append")
			.start()

		query.awaitTermination()
	}
}