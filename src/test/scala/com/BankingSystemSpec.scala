package com

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import com.knoldus.Actors
import com.knoldus.Actors.{ConfigMaxBalanceAllowance, DepositMoney, GetBalance, WithdrawMoney}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class BankingSystemSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("AkkaQuickstartSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A send request to actor" should {
    "successfully return available balance" in {

      implicit val timeout = Timeout(10 seconds)
      val testProbe = TestProbe()
      val testProbes = system.actorOf(Props[Actors])
      testProbe.send(testProbes,ConfigMaxBalanceAllowance(2000.00))
      testProbe.send(testProbes,DepositMoney(200.00))
      testProbe.send(testProbes,GetBalance())
      val future = testProbes ? GetBalance()
      val result = Await.result(future, timeout.duration)
      testProbe.expectMsg(result)
    }
    "successfully return balance after withdraw" in {

      implicit val timeout = Timeout(10 seconds)
      val testProbe = TestProbe()
      val testProbes = system.actorOf(Props[Actors])
      testProbe.send(testProbes,ConfigMaxBalanceAllowance(2000.00))
      testProbe.send(testProbes,DepositMoney(200.00))
      testProbe.send(testProbes,WithdrawMoney(200.00))
      testProbe.send(testProbes,GetBalance())
      val future = testProbes ? GetBalance()
      val result = Await.result(future, timeout.duration)
      testProbe.expectMsg(result)
    }

    "unable to add amount" in {

      implicit val timeout = Timeout(10 seconds)
      val testProbe = TestProbe()
      val testProbes = system.actorOf(Props[Actors])
      testProbe.send(testProbes,ConfigMaxBalanceAllowance(2000.00))
      testProbe.send(testProbes,DepositMoney(4000.00))
      testProbe.send(testProbes,GetBalance())
      val future = testProbes ? GetBalance()
      val result = Await.result(future, timeout.duration)
      testProbe.expectMsg(result)
    }

  }

}
