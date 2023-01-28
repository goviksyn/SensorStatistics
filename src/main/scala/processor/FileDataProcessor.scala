package processor

import java.io.{File, IOException}
import cats.effect.{ContextShift, IO}
import cats.implicits._
import com.github.tototoshi.csv.CSVReader
import model.{Measurement, ResultCollector}

trait FileDataProcessor {

  def run(directoryName: String): IO[ResultCollector]

  def getInputFiles(directoryName: String): Seq[File] = {
    val inputDirectory = new File(directoryName)
    if (!inputDirectory.isDirectory) throw new IllegalArgumentException("Invalid directory")
    inputDirectory.listFiles((_, name) => name.endsWith(".csv")).toIndexedSeq
  }

  protected def processDataFile(file: File, outputData: ResultCollector = ResultCollector()): IO[ResultCollector] = {
    val csvIO = IO(CSVReader.open(file))

    csvIO.bracket { reader =>
      IO({
        val fileOutputData = reader.toStream.tail
          .map {
            case id :: "NaN" :: Nil => Measurement(id, None)
            case id :: humidity :: Nil => Measurement(id, humidity.toInt.some)
            case _ => throw new IOException(s"Invalid file '${file.getName}'")
          }
          .foldLeft(outputData)(_ addMeasurementToData _)

        fileOutputData.copy(fileCount = fileOutputData.fileCount + 1)
      })
    } {
      in => IO(in.close()).handleErrorWith(_ => IO.unit).void
    }
  }
}

object FileDataProcessor {
  def apply()(implicit cs: ContextShift[IO]): FileDataProcessor = {
    new AkkaContentProcessor();
  }
}