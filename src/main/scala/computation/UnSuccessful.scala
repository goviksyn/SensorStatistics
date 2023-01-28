package computation

/**
 * The UnSuccessful case object is a subtype of the Aggregation trait, representing an unsuccessful aggregation of data.
 * It is used when a value is not present or invalid. So it will not update the min, max, sum, count of the data.
 */
case object UnSuccessful extends Aggregation {

  def sum(rhs: Option[Int]): Aggregation = rhs match {
    case None => this
    case Some(v) => Successful(v, v, v, 1)
  }


  def join(rhs: Aggregation): Aggregation = rhs

  override def toString: String = "NaN,NaN,NaN"


  override def average: Option[Int] = None
}
