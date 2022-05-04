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

  val parentWhenStmt = cur_module.currentWhenStmt.clone()
  val parentInWhenScope = cur_module.currentInWhenScope.clone()
  cur_module.currentWhenStmt.clear()
  cur_module.currentInWhenScope.clear()

  cur_module.whenDepth += 1

  cond match {
    case Some(c) =>
      if (cur_module.whenPredDepth.contains(cur_module.whenDepth)) {
        cur_module.whenPredDepth(cur_module.whenDepth) += c().ref
      } else {
        cur_module.whenPredDepth(cur_module.whenDepth) = ArrayBuffer(c().ref)
      }
      val cond = WhenBegin(c().ref, isFirstWhen)
      cur_module.currentWhenStmt += cond
      cur_module.whenScopeCond += cond
    case None =>
      val otherwise = OtherwiseBegin(cur_module.whenPredDepth(cur_module.whenDepth).toSeq)
      cur_module.currentWhenStmt += otherwise
      cur_module.whenScopeCond   += otherwise
  }


  try {
    block
  } catch {
    case ret: scala.runtime.NonLocalReturnControl[_] =>
      throwException("Cannot exit from a when() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
      )
  }

  cur_module.whenDepth -= 1

  cond match {
    case Some(c) =>
      val when_end = WhenEnd(cur_module.whenDepth)
      cur_module.currentInWhenScope foreach { bits =>
        cur_module.pushWhenScope(bits, when_end)
      }
      cur_module.bitsInWhenScope foreach { case (bits, depth) =>
        if (cur_module.whenDepth < depth) {
          val scope = if (cur_module.switch_id.nonEmpty) cur_module.whenScopeInSwitch else cur_module.whenScope
          if (scope(bits).last != when_end)
            scope(bits) += when_end
        }
      }
    case None =>
      cur_module.currentInWhenScope foreach { bits =>
        cur_module.pushWhenScope(bits, OtherwiseEnd(cur_module.whenDepth))
      }
  }

  if (cur_module.whenDepth == 0) {
    cur_module.whenScopeBegin = false
    cur_module.whenScopeCond.clear()
    cur_module.bitsInWhenScope.clear()
  }

  cur_module.whenScopeCond.dropRight(1)

  cur_module.currentWhenStmt    = parentWhenStmt.clone()
  cur_module.currentInWhenScope = parentInWhenScope.clone()
}
