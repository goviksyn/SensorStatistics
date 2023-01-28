package model

import cats.Semigroup
import cats.implicits._
import computation.{Aggregation, UnSuccessful}
import implicits._

import scala.collection.immutable.HashMap
import scala.util.Properties


case class ResultCollector(
                            sensors: Map[String, Aggregation] = HashMap().withDefaultValue(UnSuccessful),
                            failedCount: Int = 0,
                            totalCount: Int = 0,
                            fileCount: Int = 0
                     ) {

  /**
   * Below function called addMeasurementToData that takes a single parameter,
   * rhs, which is a Measurement object. The function returns a ResultCollector.
   * The function updates the data in the ResultCollector object by adding the humidity value
   * from the rhs measurement to the existing data for the sensor with the same ID.
   * It also increments the failedCount and totalCount fields if the humidity value is not defined,
   * otherwise it keeps the same value. The fileCount field remains the same.
   * @param rhs
   * @return
   */
  def addMeasurementToData(rhs: Measurement): ResultCollector = {
    ResultCollector(
      sensors + (rhs.id -> (sensors(rhs.id) sum rhs.humidity)),
      failedCount + (if (rhs.humidity.isDefined) 0 else 1),
      totalCount + 1,
      fileCount
    )
  }

  /**
   * Below function called outputDataToData that takes a single parameter, rhs,
   * which is a ResultCollector object. The function returns a ResultCollector.
   * The function merges the data in rhs with the data in the current ResultCollector object
   * by using the |+| operator to merge the sensors maps, adding the values of failedCount,
   * totalCount and fileCount fields from rhs to the existing values in the current ResultCollector.
   * @param rhs
   * @return
   */
  def outputDataToData(rhs: ResultCollector): ResultCollector = {
    ResultCollector(
      sensors |+| rhs.sensors,
      failedCount + rhs.failedCount,
      totalCount + rhs.totalCount,
      fileCount + rhs.fileCount
    )
  }

  /**
   * Below function toString method for the ResultCollector class. It returns a string representation of
   * the object's data, including number of processed files, processed measurements, failed measurements, and
   * the sensors data sorted by average humidity. It uses an ordering variable to sort the sensors data by average humidity.
   * @return
   */
  override def toString: String = {

    /**
     * Custom ordering
     */
    implicit val ordering: Ordering[(String, Aggregation)] =
      (x: (String, Aggregation), y: (String, Aggregation)) => (x._2.average, y._2.average) match {
        case (None, None) => 0
        case (None, _) => 1
        case (_, None) => -1
        case (Some(a), Some(b)) => b compare a
      }

    s"""Num of processed files: $fileCount
       |Num of processed measurements: $totalCount
       |Num of failed measurements: $failedCount
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |${
      sensors.toSeq.sorted.map { case (id, data) => s"$id,$data" }.mkString(Properties.lineSeparator)
    }""".stripMargin
  }
}


trait OutputDataSemigroupImpl extends Semigroup[ResultCollector] {
  def combine(lhs: ResultCollector, rhs: ResultCollector): ResultCollector = lhs outputDataToData rhs
}


trait OutputDataSemigroup {

  implicit object outputDataSemigroupImpl extends OutputDataSemigroupImpl

}