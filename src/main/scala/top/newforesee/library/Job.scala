package top.newforesee.library

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
 * Created by newforesee on 2020/6/11
 */
trait Job {

  val logger: Logger = Logger.getLogger(this.getClass)

  val spark: SparkSession = Util.getSpark(this.getClass)

  val sc: SparkContext = spark.sparkContext

  val sqlContext: SQLContext = spark.sqlContext

  sc.setLogLevel("WARN")

  def main(args: Array[String]): Unit = {
    val start: Long = System.currentTimeMillis()

    logger.log(Level.WARN, s"Starting Job.")
    // Try catch here
    run()
    logger.log(Level.WARN, s"Finished Job in ${(System.currentTimeMillis() - start) / 1000} seconds.")

  }

  /**
   * Intended to be implemented for every job created.
   */
  def run()

}
