package knitkit

import internal._

class AliasedAggregateFieldException(message: String) extends knitkitException(message)

class Aggregate(eles: Seq[(String, Data)]) extends Data with AggOps {
  val elements: Map[String, Data] = eles.toMap

  val duplicates = getElements.groupBy(identity).collect { case (x, elts) if elts.size > 1 => x }
  if (!duplicates.isEmpty) {
    throw new AliasedAggregateFieldException(s"Aggregate $this contains aliased fields $duplicates")
  }

  def apply(name: String) = elements(name)

  def _onModuleClose: Unit = {
    for ((name, elt) <- elements) { elt.setRef(this, elt.suggestedName.orElse(Some(name)).get) }
  }

  def getElements: Seq[Data] = elements.toIndexedSeq.map(_._2)

  def bind(target: Binding): Unit = {
    binding = target
  }

  def clone(fn: (Bits, Bits) => Bits = (x, y) => x): Aggregate = {
    new Aggregate((elements map { case (name, data) =>
      val clone_data = data match {
        case b: Bits      => b.clone(fn)
        case a: Aggregate => a.clone(fn)
      }
      name -> clone_data
    }).toSeq)
  }
}

object Agg {
  def apply(a: (String, Data), r: (String, Data)*): Aggregate = apply(a :: r.toList)
  def apply(your_eles: Seq[(String, Data)]): Aggregate = {
    val named_eles = your_eles map { case (n, d) => n -> d.suggestName(n) }
    new Aggregate(named_eles)
  }
}


trait AggOps { this: Aggregate =>

  def not_def_op = Builder.error(s"Not define Ops, use Bits!")

  def +  (that: Data): Bits = not_def_op
  def +& (that: Data): Bits = not_def_op
  def -  (that: Data): Bits = not_def_op
  def &  (that: Data): Bits = not_def_op
  def |  (that: Data): Bits = not_def_op
  def ^  (that: Data): Bits = not_def_op
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
}
