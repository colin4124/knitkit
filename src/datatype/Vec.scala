package knitkit

import internal._
import internal.Builder.error
import Utils._

class Vec(eles: Seq[Data]) extends Data with VecOps {
  val elements: Seq[Data] = eles

  val duplicates = getElements.groupBy(identity).collect { case (x, elts) if elts.size > 1 => x }
  if (!duplicates.isEmpty) {
    throw new AliasedAggregateFieldException(s"Aggregate $this contains aliased fields $duplicates")
  }

  def apply(name: String): Data = error(s"Vec Not Support string extract")

  def apply(idx: Int) = {
    val e = elements(idx)
    e.used = true
    e
  }

  def _onModuleClose: Unit = {
    for (elt <- elements) { elt.setRef(this, elt.computeName(None, "")) }
  }

  def prefix(s: String): this.type = {
    for (elt <- elements) { elt.prefix(s) }
    this
  }

  def suffix(s: String): this.type = {
    for (elt <- elements) { elt.suffix(s) }
    this
  }

  def flip: this.type = {
    for (elt <- elements) { elt.flip }
    this
  }

  def getElements: Seq[Data] = elements

  def bind(target: Binding): Unit = {
    binding = target
  }

  def clone(fn: (Bits, Bits) => Bits = (x, y) => x): Vec = {
    val vec = new Vec(elements map { data =>
      val clone_data = data match {
        case b: Bits      => b.clone(fn)
        case a: Aggregate => a.clone(fn)
        case v: Vec       => v.clone(fn)
      }
      clone_data
    })
    vec.decl_name      = decl_name
    vec.bypass         = bypass
    vec.suggested_name = suggested_name
    vec
  }
}

object Vec {
  def apply(total: Int, ele: Data, offset: Int = 0): Vec = {
    val begin_idx = offset
    val end_idx   = total + offset
    val eles = (begin_idx until end_idx) map { idx =>
      val clone_data: Data = ele match {
        case b: Bits      => b.clone(clone_fn_base)
        case a: Aggregate => a.clone(clone_fn_base)
        case v: Vec       => v.clone(clone_fn_base)
        case _ => ele
      }
      clone_data.suffix(s"$idx")
    }
    apply(eles)
  }
  def apply(a: Data, r: Data*): Vec = apply(a :: r.toList)
  def apply(your_eles: Seq[Data]): Vec = {
    new Vec(your_eles)
  }

  def apply(a: (String, Data), r: (String, Data)*): Vec = {
    val your_eles = a :: r.toList
    val named_eles = your_eles map { case (n, d) => d.suggestName(n) }
    new Vec(named_eles)
  }
}


trait VecOps { this: Vec =>

  def not_def_op = Builder.error(s"Not define Ops, use Bits!")

  def +  (that: Data): Bits = not_def_op
  def +& (that: Data): Bits = not_def_op
  def -  (that: Data): Bits = not_def_op
  def &  (that: Data): Bits = not_def_op
  def |  (that: Data): Bits = not_def_op
  def ^  (that: Data): Bits = not_def_op
  def *  (that: Data): Bits = not_def_op
  def /  (that: Data): Bits = not_def_op
  def %  (that: Data): Bits = not_def_op

  def <   (that: Data): Bits = not_def_op
  def >   (that: Data): Bits = not_def_op
  def <=  (that: Data): Bits = not_def_op
  def >=  (that: Data): Bits = not_def_op
  def =/= (that: Data): Bits = not_def_op
  def === (that: Data): Bits = not_def_op

  def || (that: Data): Bits = not_def_op
  def && (that: Data): Bits = not_def_op

  def << (that: Int ): Bits = not_def_op
  def << (that: Data): Bits = not_def_op

  def >> (that: Int ): Bits = not_def_op
  def >> (that: Data): Bits = not_def_op

  def asBool  = not_def_op
  def asClock = not_def_op
  def asReset = not_def_op
  def asAsyncPosReset = not_def_op
  def asAsyncNegReset = not_def_op
}
