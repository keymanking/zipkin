package com.baidu.zipkin.elasticsearch

import com.twitter.zipkin.builder.Builder
import com.twitter.zipkin.storage.Index
import com.baidu.zipkin.storage.elasticsearch._

case class IndexBuilder() extends Builder[Index] { 

  def apply() = {
    ElasticsearchIndex()
  }
}
