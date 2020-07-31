package top.newforesee.job

import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import top.newforesee.constants.Constants
import top.newforesee.library.Job

/**
 * Created by newforesee on 2020/7/31
 */
object MoviesJob extends Job {

  import spark.implicits._

  override def run(): Unit = {
    val usersRdd: RDD[String] = sc.textFile(s"${Constants.warehousePath}/users.dat")
    val moviesRdd: RDD[String] = sc.textFile(s"${Constants.warehousePath}/movies.dat")
    val ratingsRdd: RDD[String] = sc.textFile(s"${Constants.warehousePath}/ratings.dat")

    val userDF: DataFrame = mkDF(usersRdd, "UserID" :: "Gender" :: "Age" :: "Occupation" :: "Zipcode" :: Nil)
    val movies: DataFrame = mkDF(moviesRdd, "MovieID" :: "Title" :: "Genres" :: Nil)
    val ratings: DataFrame = mkDF(ratingsRdd, "UserID" :: "MovieID" :: "Rating" :: "Timestamped" :: Nil)

    saveToES(movies,"movies")

    val most_rated_times: DataFrame = ratings.select($"MovieID")
      .groupBy("MovieID")
      .count()
      .join(movies, Seq("MovieID"), "left")
      .select("Title", "count")
      .orderBy($"count" desc)

    //分别求男性，女性当中评分最高的10部电影（性别，电影名，影评分）
    val top10ByGender: DataFrame =
    ratings.join(userDF, Seq("UserID"), "left")
      .withColumn("rank", row_number().over(Window.partitionBy("Gender").orderBy(desc("Rating"))))
      .where($"rank" <= 10)
      .join(movies, Seq("MovieID"), "left")
      .select("Gender", "Title", "Rating")
      .withColumn("Gender", when($"Gender" === "M", lit("nan"))
        .otherwise("nv")
      )

//    saveToES(most_rated_times,"most_rated_times")
//    saveToES(top10ByGender,"top10_by_gender")


  }


  def saveToES(df: DataFrame, index: String) = {
    df.write
      .options(Constants.esOptions)
      .mode(SaveMode.Overwrite)
      .format("org.elasticsearch.spark.sql")
      .save(index)
  }


  /**
   * This method builds a DataFrame using the incoming RDD and schema and returns it
   * Where, the "::" split is required between each field of RDD
   *
   * @param rdd
   * @param schema
   * @return
   */
  def mkDF(rdd: RDD[String], schema: Seq[String]) = {
    val rowRdd: RDD[Row] = rdd.map(x => {
      Row.fromSeq(x.split("::"))
    })

    val fields: Seq[StructField] = for {
      elem <- schema
      st = StructField(elem, StringType, true)
    } yield st
    sqlContext.createDataFrame(rowRdd, StructType(fields))
  }


}
