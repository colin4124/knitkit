package knitkit

import collection.mutable.HashMap

import internal._
import ir._

case class Instance(port_map: Map[String, Data]) extends HasId {
  def clone_fn(clone: Bits, orig: Bits): Bits = {
    clone.bind(orig.binding)
    clone._prefix ++= orig._prefix
    clone._suffix ++= orig._suffix
    clone.decl_name = orig.decl_name
    clone.bypass    = orig.bypass
    clone.suggested_name = orig.suggested_name
    clone.direction = orig.direction
    clone.setRef(InstanceIO(this, orig.computeName(None, "INST_IO")))
    clone
  }

  val agg_ports = HashMap[String, Aggregate]()
  val bit_ports = HashMap[String, Bits]()

  val ports = port_map map { case (n, d) => d match {
	  case a: Aggregate =>
      n -> {
        val agg = a.clone(clone_fn _)
        agg.bind(a.binding)
        agg.setRef(InstanceIO(this, a.suggestedName.get))
        agg
      }
	  case b: Bits =>
      n -> b.clone(clone_fn _)
    }
  }

  def apply(port: String): Data = {
    val p = ports(port)
    p.bypass = false
    p.used = true
    p
  }
}
