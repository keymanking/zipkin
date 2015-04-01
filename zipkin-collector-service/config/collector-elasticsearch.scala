/*
 * Copyright 2012 Twitter Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.twitter.logging._
import com.twitter.logging.BareFormatter
import com.twitter.zipkin.builder.{ZipkinServerBuilder, Scribe}
import com.twitter.zipkin.cassandra
import com.baidu.zipkin.elasticsearch
import com.twitter.zipkin.collector.builder.CollectorServiceBuilder
import com.twitter.zipkin.storage.Store
import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import org.elasticsearch.common.settings.ImmutableSettings

val loggers = List(LoggerFactory(level = Some(Level.INFO),
  handlers = List(FileHandler(filename = "./logs/zipkin-collector.log",
    rollPolicy = Policy.Daily,
    append = true,
    formatter = BareFormatter))))

val keyspaceBuilder = cassandra.Keyspace.static(nodes = Set("localhost"))
val cassandraBuilder = Store.Builder(
  cassandra.StorageBuilder(keyspaceBuilder),
  cassandra.IndexBuilder(keyspaceBuilder),
  cassandra.AggregatesBuilder(keyspaceBuilder)
)

val settings = ImmutableSettings.settingsBuilder()
  //.put("cluster.name", "dtrace")
  //.put("client.transport.sniff", true)
  .build()

val uri = ElasticsearchClientUri("elasticsearch://localhost:8300,localhost:8300")
val elasticsearchClient = ElasticClient.remote(settings, uri)
val elasticsearchBuilder = Store.Builder(
  elasticsearch.StorageBuilder(elasticsearchClient),
  elasticsearch.IndexBuilder()
)

CollectorServiceBuilder(Scribe.Interface(categories = Set("zipkin")))
  .writeTo(cassandraBuilder)
  .writeTo(elasticsearchBuilder)
  .copy(queueMaxSize = 100000, queueNumWorkers = 15 , serverBuilder =  ZipkinServerBuilder(9410, 9900).loggers(loggers))
