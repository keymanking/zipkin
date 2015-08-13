package com.baidu.zipkin.elasticsearch

import com.baidu.zipkin.storage.elasticsearch.ElasticsearchStorage
import com.sksamuel.elastic4s.ElasticClient
import com.twitter.zipkin.builder.Builder
import com.twitter.zipkin.storage.Storage

case class StorageBuilder(
  client: ElasticClient,
  storeDurationSpanList: Seq[String] = Seq()
) extends Builder[Storage] {

  def apply() = {

    ElasticsearchStorage(client, storeDurationSpanList)
  }
}

