package com.knoldus

import akka.actor.{Actor, Props}
import org.apache.log4j.Logger

class Actors extends Actor {

  import com.knoldus.Actors._
  implicit val log = Logger.getLogger(this.getClass)

  var balance: BigDecimal = 0.0
  var maximumBalance: BigDecimal = 0.0

  override def receive: Receive = {
    case m: DepositMoney => {
      if (m.amt + balance > maximumBalance) {
        log.info("Operation is not allowed: exceeded maximum balance allowance")
      }
      else {
        balance = balance + m.amt
      }
    }

    case m: WithdrawMoney => {
      if (m.amt > balance) {
        log.info("Operation is not allowed: not enough money")
      }
      else {
        balance = balance - m.amt
      }
    }

    case m: ConfigMaxBalanceAllowance => maximumBalance = m.maxBalance
    case m: GetMaxBalanceAllowance => sender() ! maximumBalance
    case m: GetBalance => sender() ! balance
    case _ => log.info("Invalid message")
  }
}

object Actors {

  sealed trait Msg

  case class DepositMoney(amt: BigDecimal) extends Msg

  case class WithdrawMoney(amt: BigDecimal) extends Msg

  case class ConfigMaxBalanceAllowance(maxBalance: BigDecimal) extends Msg

  case class GetMaxBalanceAllowance() extends Msg

  case class GetBalance() extends Msg

}
