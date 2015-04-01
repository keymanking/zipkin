
package com.baidu.zipkin.storage.elasticsearch

import java.nio.CharBuffer
import java.nio.charset.Charset

import com.twitter.conversions.time._
import com.twitter.ostrich.stats.Stats
import com.twitter.util.{Await, Duration, Future}
import com.twitter.zipkin.common.Span
import com.twitter.zipkin.conversions.thrift._
import com.twitter.zipkin.storage.Storage
import scala.collection.JavaConverters._

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

case class ElasticsearchStorage(
  client: ElasticClient
) extends Storage {
  val decoder = Charset.forName("UTF-8").newDecoder()

  def close() {
  }

  def storeSpan(span: Span): Future[Unit] = {

    //val client = ElasticClient.remote("localhost",9300)
    //ToDo : proto include product name
    val productName = "www"
    val serviceName = span.clientSideEndpoint.get.serviceName

    val metaFields = Map(
      "trace_id" -> span.traceId,
      "host" -> span.clientSideEndpoint.get.getHostAddress,
      "port" -> span.clientSideEndpoint.get.port,
      "service_name" -> serviceName
    )
    // filter out xxx.log in binaryAnnotation
    // decode binaryAnnotation value
    val binaryAnnotationFields = span.binaryAnnotations.filter(!_.key.endsWith("log")).
      map( b => b.key -> decoder.decode(b.value))

    Future {
      client.execute {
        // index metaFields & binaryAnnotationFields
        index into productName / serviceName fields metaFields ++ binaryAnnotationFields
      }.await
      // return Unit
      ()
    } onSuccess { (_) =>
      println("send to es success")
      println(span.traceId)
    } onFailure { (_) =>
      println("error")
    }
  }
  def setTimeToLive(traceId: Long, ttl: Duration): Future[Unit] = {
    Future.Unit
  }

  def getTimeToLive(traceId: Long): Future[Duration] = {
    Future(Duration.Zero)
  }

  def getSpansByTraceId(traceId: Long) : Future[Seq[Span]] = {
    Future(Seq.empty[Span])
  }

  def getSpansByTraceIds(traceIds: Seq[Long]): Future[Seq[Seq[Span]]] = {
    Future(Seq(Seq.empty[Span]))
  }

  def getDataTimeToLive: Int = {
    Int.MaxValue
  }

  def tracesExist(traceIds: Seq[Long]): Future[Set[Long]] = {
    Future(Set.empty[Long])
  }
}
