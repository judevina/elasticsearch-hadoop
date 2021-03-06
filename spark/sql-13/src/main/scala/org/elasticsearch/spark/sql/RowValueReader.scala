package org.elasticsearch.spark.sql

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap
import org.elasticsearch.hadoop.cfg.Settings
import org.elasticsearch.hadoop.serialization.SettingsAware
import org.elasticsearch.hadoop.util.StringUtils
import scala.collection.mutable.Buffer

private[sql] trait RowValueReader extends SettingsAware {

  protected var readMetadata = false 
  var metadataField = ""
  protected var rowMap: scala.collection.Map[String, Buffer[String]] = Map.empty
  protected var currentField = MappingUtils.ROOT_LEVEL_NAME
  
  abstract override def setSettings(settings: Settings) = {
    super.setSettings(settings)
    
    val csv = settings.getScrollFields
    readMetadata = settings.getReadMetadata
    rowMap = MappingUtils.getRowOrder(settings)
  }
  
  def rowOrder(currentField: String): Buffer[String] = {
    rowMap.get(currentField).get
  }
  
  def addToBuffer(esRow: ScalaEsRow, key: AnyRef, value: Any) {
    val pos = esRow.rowOrder.indexOf(key.toString())
    esRow.values.update(pos, value)
  }
}