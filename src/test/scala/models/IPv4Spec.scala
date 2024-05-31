package models

import eu.timepit.refined.api.Refined
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import models.IPv4._

class IPv4Spec extends AnyFlatSpec with Matchers {
  val validIP1 = "127.0.0.1"
  val validIP2 = "0.0.0.0"
  val validIP3 = "255.255.255.255"

  val invalidIP1 = "256.256.256.256"
  val invalidIP2 = "ip"
  val invalidIP3 = "127001"
  val invalidIP4 = ""
  val invalidIP5 = "0"

  "validateIPv4" should "validate correct IPv4 strings" in {
    validateIPv4(validIP1) shouldBe Right(Refined.unsafeApply(validIP1))
    validateIPv4(validIP2) shouldBe Right(Refined.unsafeApply(validIP2))
    validateIPv4(validIP3) shouldBe Right(Refined.unsafeApply(validIP3))
  }

  it should "invalidate incorrect IPv4 strings" in {
    validateIPv4(invalidIP1).isLeft shouldBe true
    validateIPv4(invalidIP2).isLeft shouldBe true
    validateIPv4(invalidIP3).isLeft shouldBe true
    validateIPv4(invalidIP4).isLeft shouldBe true
    validateIPv4(invalidIP5).isLeft shouldBe true
  }

  "validateIPv4Address" should "validate correct IPv4 addresses and wrap them" in {
    validateIPv4Address(validIP1) shouldBe Right(IPv4Address(Refined.unsafeApply(validIP1)))
    validateIPv4Address(validIP2) shouldBe Right(IPv4Address(Refined.unsafeApply(validIP2)))
    validateIPv4Address(validIP3) shouldBe Right(IPv4Address(Refined.unsafeApply(validIP3)))
  }

  it should "invalidate incorrect IPv4 addresses and provide error messages" in {
    validateIPv4Address(invalidIP1).isLeft shouldBe true
    validateIPv4Address(invalidIP2).isLeft shouldBe true
    validateIPv4Address(invalidIP3).isLeft shouldBe true
    validateIPv4Address(invalidIP4).isLeft shouldBe true
    validateIPv4Address(invalidIP5).isLeft shouldBe true
  }
}
