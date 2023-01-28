package processor

import java.io.{File, IOException}
import cats.effect.{ContextShift, IO}
import cats.implicits._
import com.github.tototoshi.csv.CSVReader
import model.{Measurement, ResultCollector}

trait FileDataProcessor {

  def run(directoryName: String): IO[ResultCollector]

  /**
   * The getInputFiles method takes in a directory name and returns a sequence of files that are present
   * in that directory and have the ".csv" extension.
   * It first checks if the directory is valid, throws an exception if it is not.
   * It then filters the files in the directory by csv extensions and returns the filtered files as a sequence.
   * @param directoryName
   * @return
   */
  def getInputFiles(directoryName: String): Seq[File] = {
    val inputDirectory = new File(directoryName)
    if (!inputDirectory.isDirectory) throw new IllegalArgumentException("Invalid directory")
    inputDirectory.listFiles((_, name) => name.endsWith(".csv")).toIndexedSeq
  }

  /**
   * The processDataFile method that takes in a file and an object that collects the results of processing the file.
   * It uses the bracket method to safely acquire and release a resource,
   * in this case, the CSV file. Inside the bracket, it reads the file, extracts the required data, and adds it to the
   * outputData object.
   * It increments the fileCount field of the outputData object. Finally, the method returns the outputData object,
   * which is wrapped in an IO effect.
   * @param file
   * @param outputData
   * @return
   */
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
