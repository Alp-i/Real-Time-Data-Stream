package com.project.homework2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Component;

@Component
public class ExpenseService{
    private final SparkSession spark;

    public ExpenseService(@Value("${spark.cassandra.connection.host}") String cassandraHost) {
        this.spark = SparkSession.builder()
                .appName("Expense Records Fetcher")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.input.consistency.level", "ONE")
                .config("spark.cassandra.output.consistency.level", "ONE")
                .master("local[*]")
                .getOrCreate();
    }

    public Dataset<Row> getTotalPaymentByEmpno() {
        // Load the expense records from Cassandra
        Dataset<Row> expenseData = spark.read()
                .format("org.apache.spark.sql.cassandra")
                .option("table", "expenses")
                .option("keyspace", "emp")
                .load();

        return expenseData.groupBy("userid")
                .agg(functions.sum("payment").alias("total_payment"));
    }

    public Dataset<Row> getExpenseReportByUserId(int userId) {
        Dataset<Row> expenseData = spark.read()
                .format("org.apache.spark.sql.cassandra")
                .option("table", "expenses")
                .option("keyspace", "emp")
                .load();

        return expenseData.filter(functions.col("userid").equalTo(userId))
                .select("count", "description", "expensetype", "payment", "timestamp", "userid");
    }
}
