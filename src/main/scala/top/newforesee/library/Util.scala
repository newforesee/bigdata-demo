package top.newforesee.library

import java.io.File

import org.apache.spark.sql.SparkSession

/**
 * xxx
 * creat by newforesee 2020/1/14
 */
object Util {

  def getSpark(clazz:Any) ={
    SparkSession.builder().master("local").appName(clazz.getClass.getSimpleName).getOrCreate()
  }
  def traverseFile(dir: File): Iterator[File] = {
    val children: Array[File] = dir.listFiles
    children.toIterator

  }

}
