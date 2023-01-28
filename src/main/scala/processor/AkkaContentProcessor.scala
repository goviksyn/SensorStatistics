package processor

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import cats.implicits._
import implicits._
import cats.effect._
import model.ResultCollector

import java.io.File
import scala.concurrent.{ExecutionContextExecutor, Future}



class AkkaContentProcessor(implicit cs: ContextShift[IO]) extends FileDataProcessor {

  /**
   * @param directoryName
   *
   * Below is a function called run that takes a directory name and returns an IO object with a ResultCollector.
   * The function uses the Akka library to create an ActorSystem and an ExecutionContextExecutor for concurrency and async
   * operations. The function also uses the getInputFiles method to get a list of files in the directory, and then uses
   * a flow with a parallelism level of 8 to process the files asynchronously using the processDataFile method and
   * combine the results using the |+| operator. The function also sets up a callback to terminate the ActorSystem when
   * the processing is complete, and wraps the result in an IO monad before returning it.
   * @param cs
   * @return
   */
  def run(directoryName: String): IO[ResultCollector] = IO.fromFuture(IO {
    implicit val system: ActorSystem = ActorSystem("SensorStatistics")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val parallelism = 8
    val source = Source(getInputFiles(directoryName))
    val future = source
      .via(Flow[File].mapAsync(parallelism)(file => Future(processDataFile(file))))
      .runWith(Sink.fold(IO(ResultCollector()))(_ |+| _))

    future.onComplete(_ => system.terminate())
    future
  }).flatMap(identity)
}
