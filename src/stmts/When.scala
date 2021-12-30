package knitkit

import collection.mutable.{HashMap, ArrayBuffer, Set}

import ir._
import internal._

object when {
  def apply(cond: => Bits)(block: => Unit): WhenContext = {
    new WhenContext(Some(() => cond), block, true)
  }
}

final class WhenContext(cond: Option[() => Bits], block: => Unit, isFirstWhen: Boolean = false) {

  val cur_module = Builder.forcedUserModule

  def elsewhen (elseCond: => Bits)(block: => Unit): WhenContext = {
    new WhenContext(Some(() => elseCond), block)
  }

  def otherwise(block: => Unit): Unit =
    new WhenContext(None, block)

  if (cur_module.whenDepth == 0) cur_module.whenScopeBegin = true

  val parentWhenStmt = cur_module.currentWhenStmt
  val parentInWhenScope = cur_module.currentInWhenScope
  cur_module.currentWhenStmt.clear()
  cur_module.currentInWhenScope.clear()

  cond match {
    case Some(c) =>
      cur_module.currentWhenStmt += WhenBegin(c().ref, isFirstWhen)
    case None =>
      cur_module.currentWhenStmt += OtherwiseBegin()
  }

  cur_module.whenDepth += 1

  try {
    block
  } catch {
    case ret: scala.runtime.NonLocalReturnControl[_] =>
      throwException("Cannot exit from a when() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
      )
  }

  cur_module.whenDepth -= 1
  if (cur_module.whenDepth == 0) cur_module.whenScopeBegin = false

  cond match {
    case Some(c) =>
      cur_module.currentInWhenScope foreach { bits =>
        cur_module.pushWhenScope(bits, WhenEnd())
      }
    case None =>
      cur_module.currentInWhenScope foreach { bits =>
      cur_module.pushWhenScope(bits, OtherwiseEnd())
    }
  }

  cur_module.currentWhenStmt    = parentWhenStmt
  cur_module.currentInWhenScope = parentInWhenScope
}
