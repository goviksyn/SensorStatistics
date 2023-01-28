package computation

import computation.{Successful, UnSuccessful}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class AggregationSpec extends AnyFlatSpec with should.Matchers {
  "A valid aggregation" should "be the same after combining with failed aggregation" in {
    val valid = Successful(2, 10, 12, 2)
    val combined = valid join UnSuccessful
    combined should be(valid)
  }

  it should "be valid aggregation after combining with valid aggregation" in {
    val valid0 = Successful(2, 10, 12, 2)
    val valid1 = Successful(1, 15, 20, 3)
    val combined = valid0 join valid1
    combined should be(Successful(1, 15, 32, 5))
  }

  "A failed aggregation" should "be equal to the aggregation, with which it combines" in {
    val valid = Successful(2, 10, 12, 2)
    val combined = UnSuccessful join valid
    combined should be(valid)
  }

  it should "be be failed after combining with failed aggregation" in {
    val combined = UnSuccessful join UnSuccessful
    combined should be(UnSuccessful)
  }
}
