package knitkit

import collection.mutable.HashMap

import internal._
import ir._
import Utils._

case class Instance(port_map: Map[String, Data]) extends HasId {
  def clone_fn(clone: Bits, orig: Bits): Bits = {
    val new_clone = clone_fn_all(clone, orig)
    new_clone.setRef(InstanceIO(this, orig.computeName(None, "INST_IO")))
    new_clone
  }

  val ports = port_map map { case (n, d) => d match {
    case v: Vec =>
      n -> {
        val vec = v.clone(clone_fn _)
        v.bind(v.binding)
        v.setRef(InstanceIO(this, v.computeName(None, "INST_VEC_IO")))
        v
      }
	  case a: Aggregate =>
      n -> {
        val agg = a.clone(clone_fn _)
        agg.bind(a.binding)
        agg.setRef(InstanceIO(this, a.computeName(None, "INST_AGG_IO")))
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
