package models

import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.string.IPv4
import io.estatico.newtype.macros.newtype


object IPv4 {
  private type IPv4String = String Refined IPv4 // strings that compile with IPv4 format
  private object IPv4String extends RefinedTypeOps[IPv4String, String] // adds functionalities to custom IPv4String type, such as `from` method

  @newtype case class IPv4Address(value: IPv4String) // zero-cost wrapper of IPv4String

  def validateIPv4(ip: String): Either[String, IPv4String] = IPv4String.from(ip)

  def validateIPv4Address(ip: String): Either[String, IPv4Address] = {
    validateIPv4(ip).map(IPv4Address.apply)
  }
}