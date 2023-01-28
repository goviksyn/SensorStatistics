package computation

import cats.Semigroup


trait Aggregation {

  def sum(rhs: Option[Int]): Aggregation

  def join(rhs: Aggregation): Aggregation

  def average: Option[Int]
}

trait AggregationSemigroupImpl extends Semigroup[Aggregation] {
  def combine(lhs: Aggregation, rhs: Aggregation): Aggregation = lhs join rhs
}


trait AggregationSemigroup {

  implicit object aggregationSemigroupImpl extends AggregationSemigroupImpl

}