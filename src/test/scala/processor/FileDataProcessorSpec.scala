package processor

import java.io.IOException
import cats.effect.{ContextShift, IO}
import cats.implicits._
import computation.{Successful, UnSuccessful}
import model.ResultCollector
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext

class FileDataProcessorSpec extends AnyFlatSpec with should.Matchers {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val directoryName = getClass.getResource("/default").getPath

  val defaultOutput: ResultCollector = ResultCollector(
    HashMap(
      "s1" -> Successful(10, 98, 108, 2),
      "s2" -> Successful(78, 88, 246, 3),
      "s3" -> UnSuccessful
    ), 2, 7, 2
  )

  trait Processor {
    val processor: FileDataProcessor = FileDataProcessor()
  }

  "Output of default test" should "be as specified in task.md including string representation" in new Processor {
    private val outputData = processor.run(directoryName).unsafeRunSync()
    outputData should be(defaultOutput)

    private def fixNewLine(s:String) = s.replaceAll("\\r\\n|\\r|\\n", "\\n")

    fixNewLine(outputData.toString) should be(
      fixNewLine("""Num of processed files: 2
        |Num of processed measurements: 7
        |Num of failed measurements: 2
        |
        |Sensors with highest avg humidity:
        |
        |sensor-id,min,avg,max
        |s2,78,82,88
        |s1,10,54,98
        |s3,NaN,NaN,NaN""".stripMargin))
  }


}
