import cats.effect._
import cats.implicits._
import processor.FileDataProcessor

/**
 * The class SensorMain extends the IOApp trait provided by the cats.effect library,
 * which allows the class to run as a command line application.
 * Below run method takes a single argument args: List[String] which is a list of command-line arguments passed
 * to the application.
 * It creates an IO effect that wraps up logic to process the data from CSV file using the FileDataProcessor class,
 * and then prints the result.
 * If any error occurs in the process, it will be handled by the handleError method and it will print the error message
 * and return ExitCode.Error.
 * If the process is successful, it will return ExitCode.Success.
 */
object SensorMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val ioExit = for {
      csvProcessor <- IO(FileDataProcessor())
      result <- csvProcessor.run(
         args
        .headOption
        .getOrElse(throw new
            IllegalArgumentException("No argument provided, Please provide command line argument like 'C:\\DataFolder'")))
      _ <- IO(println(result))
    } yield ExitCode.Success

    ioExit.handleError(throwable => {
      println(throwable.getMessage)
      ExitCode.Error
    })
  }

}