package knitkit

import ir._
import internal._

object Mux {
  def pushMux(dest: Bits, cond: Expression, tval: Expression, fval: Expression): Bits = {
    dest.bind(OpBinding(Builder.forcedUserModule))
    dest.setRef(ir.Mux(cond, tval, fval))
    dest
  }

  def apply(cond: Bits, con: Data, alt: Data): Bits = {
    (con, alt) match {
	    case (c: Bits, a: Bits) =>
        cond.width match {
          case IntWidth(x) =>
            require(x == 1)
          case UnknownWidth =>
            Builder.error(s"Cond width must be 1")
        }
        requireIsHardware(cond, "mux condition")
        requireIsHardware(c   , "mux true value")
        requireIsHardware(a   , "mux false value")
        val elt = if (cond.width == (cond.width max a.width)) c else a
        val dest = elt.cloneType
        pushMux(dest, cond.ref, c.ref, a.ref)
      case _ =>
        Builder.error(s"Mux's params type Bits only")
    }
  }
}

object MuxCase {
  def apply(default: Bits, mapping: Seq[(Bits, Bits)]): Bits = {
    var res = default
    for ((t, v) <- mapping.reverse){
      res = Mux(t, v, res)
    }
    res
  }
}
