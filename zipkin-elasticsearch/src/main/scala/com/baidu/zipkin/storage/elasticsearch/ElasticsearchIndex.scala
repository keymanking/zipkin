package com.baidu.zipkin.storage.elasticsearch

import java.nio.ByteBuffer
import com.twitter.zipkin.common.Span
import com.twitter.util.Future
import com.twitter.zipkin.storage.{Index, IndexedTraceId, TraceIdDuration}
import scala.collection.Set

case class ElasticsearchIndex extends Index {
  def close() = {}

  def getTraceIdsByName(serviceName: String, spanName: Option[String],
                       endTs: Long, limit: Int): Future[Seq[IndexedTraceId]] = {
    Future(Seq.empty[IndexedTraceId])
  }

  def getTraceIdsByAnnotation(serviceName: String, annotation: String, value: Option[ByteBuffer],
                             endTs: Long, limit: Int): Future[Seq[IndexedTraceId]] = {
    Future(Seq.empty[IndexedTraceId])
  }

  def getTracesDuration(traceIds: Seq[Long]): Future[Seq[TraceIdDuration]] = Future(Seq.empty[TraceIdDuration])

  def getServiceNames: Future[Set[String]] = Future(Set.empty[String])

  def getSpanNames(service: String): Future[Set[String]] = Future(Set.empty[String])

  def indexTraceIdByServiceAndName(span: Span) : Future[Unit] = Future.Unit

  def indexSpanByAnnotations(span: Span) : Future[Unit] = Future.Unit

  def indexServiceName(span: Span) : Future[Unit] = Future.Unit

  def indexSpanNameByService(span: Span) : Future[Unit] = Future.Unit

  def indexSpanDuration(span: Span): Future[Unit] = Future.Unit
}
