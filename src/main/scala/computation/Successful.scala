package computation

import cats.implicits._

/**
 * The class has two methods sum and join that are used to update the min, max, sum and count with new values.
 * @param min
 * @param max
 * @param sum
 * @param count
 */
case class Successful(min: Int, max: Int, sum: Int, count: Int) extends Aggregation {

  /**
   * The sum method takes an option of an integer and adds it to the sum and count if it is some.
   * @param rhs
   * @return
   */
  def sum(rhs: Option[Int]): Aggregation = rhs match {
    case None => this
    case Some(v) => Successful(math.min(min, v), math.max(max, v), sum + v, count + 1)
  }


  /**
   * The join method takes another instance of the Aggregation trait and updates the min, max, sum, and count of the current instance
   * with that of the given instance.
   * @param rhs
   * @return
   */
  def join(rhs: Aggregation): Aggregation = rhs match {
    case UnSuccessful => this
    case a: Successful => Successful(
      math.min(min, a.min),
      math.max(max, a.max),
      sum + a.sum,
      count + a.count
    )
  }

  override def toString: String = s"$min,${if (count == 0) "NaN" else average.get},$max"


  lazy val average: Option[Int] = (sum / count).some
}
