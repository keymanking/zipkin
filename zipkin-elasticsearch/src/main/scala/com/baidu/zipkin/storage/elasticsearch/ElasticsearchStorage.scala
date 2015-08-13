package com.baidu.zipkin.storage.elasticsearch

import java.nio.charset.Charset

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.twitter.util.{Duration, Future}
import com.twitter.zipkin.common.Span
import com.twitter.zipkin.storage.Storage

case class ElasticsearchStorage(
  client: ElasticClient,
  storeDurationSpanList: Seq[String]
) extends Storage {
  val decoder = Charset.forName("UTF-8").newDecoder()

  def close() {
  }

  def storeSpan(span: Span): Future[Unit] = {

    //val client = ElasticClient.remote("localhost",9300)
    // 1. in the list
    // 2. client side span
    // 3. duration > 0
    if ( span.isClientSide() && storeDurationSpanList.contains(span.name.split("_").head) ) {

      val first = span.firstAnnotation.map(_.timestamp)
      val last = span.lastAnnotation.map(_.timestamp)
      var duration = last.get - first.get

      if ( duration > 0 ) {
        val duration_index = "duration_index"
        //ToDo : data should include product name
        val productName = "ps"
        val serviceName = span.clientSideEndpoint.get.serviceName

        val metaFields = Map(
          "trace_id" -> span.traceId,
          "span_name" -> span.name,
          "host" -> span.clientSideEndpoint.get.getHostAddress,
          "service_name" -> serviceName,
          "start_time" -> span.firstTimestamp.get,
          "duration" -> span.duration.get
        )
        // filter out xxx.log in binaryAnnotation
        // decode binaryAnnotation value
        //val binaryAnnotationFields = span.binaryAnnotations.filter(!_.key.endsWith("log")).
        //  map( b => b.key -> decoder.decode(b.value))

        Future {
          client.execute {
            // index metaFields & binaryAnnotationFields
            index into duration_index / productName fields metaFields
          }
          // return Unit
          // ToDo: find a way , return Future[Unit]
          ()
        } onSuccess { (_) =>
          //println("send to es success")
          //println(span.traceId)
        } onFailure { (_) =>
          println("error")
        }
      } else {
        Future.Unit
      }

    } else {
      Future.Unit
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

  def getFanoutSpansByIds(ids: Seq[String]): Future[Seq[Seq[Span]]] = {
    Future(Seq(Seq.empty[Span]))
  }

  def getFanoutSpansById(id: String): Future[Seq[Span]] = {
    Future(Seq.empty[Span])
  }
}
