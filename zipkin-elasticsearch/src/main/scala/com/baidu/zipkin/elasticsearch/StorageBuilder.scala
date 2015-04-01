package com.baidu.zipkin.elasticsearch

import com.sksamuel.elastic4s.ElasticClient
import com.twitter.conversions.time._
import com.twitter.util.Duration
import com.twitter.zipkin.builder.Builder
import com.baidu.zipkin.storage.elasticsearch.ElasticsearchStorage
import com.twitter.zipkin.storage.Storage

case class StorageBuilder(
  client: ElasticClient
) extends Builder[Storage] {

  def apply() = {

    ElasticsearchStorage(client)
  }
}

