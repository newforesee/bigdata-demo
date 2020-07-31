package top.newforesee.constants

import java.util.Properties

/**
 * Created by newforesee on 2020/7/31
 */
object Constants {

  val warehousePath = "/Users/newforesee/IdeaProjects/bigdata_demo/ml-1m"

  val esOptions = Map(
    "es.index.auto.create" -> "true",
    "es.nodes.wan.only" -> "true",
    "es.nodes" -> "localhost",
    "es.port" -> "9200"
  )
  val properties = new Properties
  properties.load(this.getClass.getClassLoader.getResourceAsStream("mysqlconf.properties"))

}
