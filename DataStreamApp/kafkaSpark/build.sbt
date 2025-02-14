name := "Stream Handler"

version := "1.0"

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % "3.5.3",
	"org.apache.spark" %% "spark-sql" % "3.5.3" % "provided",
	"com.datastax.spark" %% "spark-cassandra-connector" % "3.5.1",
	"org.apache.cassandra" % "java-driver-core" % "4.18.1"
)