package knitkit

import ir._
import internal._

object Mux {
  def pushMux[T <: Bits](dest: T, cond: Expression, tval: Expression, fval: Expression): T = {
    dest.bind(OpBinding(Builder.forcedUserModule))
    dest.setRef(ir.Mux(cond, tval, fval))
    dest
  }

  def check(cond: Data, con: Data, alt: Data): Unit = {
    check(cond.asBits, con, alt)
  }

  def check(cond: Bits, con: Data, alt: Data): Unit = {
    cond.width match {
      case IntWidth(x) =>
        require(x == 1, s"Conditional bit width must be 1, not $x")
      case UnknownWidth =>
        Builder.error(s"Cond width must be 1")
    }
    requireIsHardware(cond, "mux condition")
    requireIsHardware(con , "mux true value")
    requireIsHardware(alt , "mux false value")
  }

  def apply[T <: Data](cond: Data, con: T, alt: T): T = {
    check(cond, con, alt)
    (con, alt) match {
      case (c: Bits, a: Bits) =>
        val elt = if (c.width == (c.width max a.width)) c else a
        val dest = elt.cloneType
        pushMux(dest, cond.ref, c.ref, a.ref).asInstanceOf[T]
      case (c: Vec, a: Vec) =>
        Vec((c.elements zip a.elements) map { case (c, a) =>
              apply(cond, c, a)
            }).asInstanceOf[T]
      case (c: Aggregate, a: Aggregate) =>
        Agg(c.eles map { case (name, ele) =>
          name -> apply(cond, ele, a(name))
        }).asInstanceOf[T]
      case _ =>
        Builder.error(s"Mux's Must both are Bits, Vec, Or Agg")

    }
  }
}

object MuxCase {
  def apply[T <: Data](default: T, mapping: Seq[(Data, T)]): T = {
    var res = default
    for ((t, v) <- mapping.reverse){
      res = Mux(t, v, res)
    }
    res
  }
}

object PriorityMux {
  def apply[T <: Data](in: Seq[(Bits, T)]): T = priorityMux(in)
  def apply[T <: Data](sel: Seq[Bits], in: Seq[T]): T = apply(sel.zip(in))
  def apply[T <: Data](sel: Bits, in: Seq[T]): T = apply((0 until in.size).map(sel(_)), in)

  def priorityMux[T <: Data](
    in: Seq[(Bits, T)]
  ): T = {
    if (in.size == 1) {
      in.head._2
    } else {
      Mux(in.head._1, in.head._2, priorityMux(in.tail))
    }
  }
}

/** Builds a Mux tree out of the input signal vector using a one hot encoded
  * select signal. Returns the output of the Mux tree.
  *
  * @example {{{
  * val hotValue = chisel3.util.Mux1H(Seq(
  *  io.selector(0) -> 2.U,
  *  io.selector(1) -> 4.U,
  *  io.selector(2) -> 8.U,
  *  io.selector(4) -> 11.U,
  * ))
  * }}}
  *
  * @note results unspecified unless exactly one select signal is high
  */
object Mux1H {
  def apply[T <: Data](sel: Seq[Bits], in: Seq[T]): T =
    apply(sel.zip(in))
  def apply[T <: Data](in:  Iterable[(Bits, T)]): T = oneHotMux(in)
  def apply[T <: Data](sel: Bits, in: Seq[T]): T =
    apply((0 until in.size).map(sel(_)), in)
  def apply(sel: Bits, in: Bits): Bits = (sel & in).orR

  def oneHotMux[T <: Data](
    in: Iterable[(Bits, T)]
  ): T = {
    if (in.tail.isEmpty) {
      in.head._2
    } else {
      val output = cloneSupertype(in.toSeq.map { _._2 }, "oneHotMux")

      def buildAndOrMultiplexor[TT <: Data](inputs: Iterable[(Bits, TT)]): T = {
        val masked = for ((s, i) <- inputs) yield Mux(s, i.asUInt, 0.U)
        masked.reduceLeft(_ | _).asInstanceOf[T]
      }

      output match {
        case b: Bits =>
          val res = b.tpe match {
            case _: SIntType =>
              val masked = for ((s, i) <- in) yield Mux(s, i, 0.S)
              masked.reduceLeft(_ | _)
            case _ =>
              val masked = for ((s, i) <- in) yield Mux(s, i.asUInt, 0.U)
              masked.reduceLeft(_ | _)
          }
          res.asInstanceOf[T]
        case agg: Aggregate =>
            val out = Wire(agg)
            val (sel, inData) = in.unzip
            val inElts = inData.map(_.asInstanceOf[Aggregate].getElements)
            // We want to iterate on the columns of inElts, so we transpose
            out.getElements.zip(inElts.transpose).foreach {
              case (outElt, elts) =>
                outElt := oneHotMux(sel.zip(elts))
            }
            out.asInstanceOf[T]
        case _ =>
          Builder.error(s"TODO")
      }
    }
  }
}
