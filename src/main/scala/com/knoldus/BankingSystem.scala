package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import com.knoldus.Actors._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

object BankingSystem extends App {

  val system = ActorSystem("simple-account")
  implicit val logger: LoggingAdapter = Logging(system, "Baile")

  var acc = system.actorOf(Props[Actors], "user-1")
  acc ! ConfigMaxBalanceAllowance(2000.00)
  acc ! DepositMoney(200.00)
  acc ! WithdrawMoney(100.00)
  acc ! WithdrawMoney(101.00)
  acc ! DepositMoney(2000.00)
  implicit val timeout = Timeout(10 seconds)
  var future = acc ? GetBalance()
  var result = Await.result(future, timeout.duration)
  logger.info(s"Current balance: ${result}")


  acc = system.actorOf(Props[Actors], "user-2")
  acc ! ConfigMaxBalanceAllowance(2000.00)
  acc ! DepositMoney(300.00)
  acc ! WithdrawMoney(100.00)
  acc ! DepositMoney(2000.00)
  future = acc ? GetBalance()
  result = Await.result(future, timeout.duration)
  logger.info(s"Current balance: ${result}")

}
