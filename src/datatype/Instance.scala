package knitkit

import collection.mutable.HashMap

import internal._
import ir._
import Utils._

case class Instance(port_map: Seq[(String, Data)]) extends HasId {
  def clone_fn(clone: Bits, orig: Bits): Bits = {
    val new_clone = clone_fn_all(clone, orig)
    new_clone.setRef(InstanceIO(this, orig.computeName(None, "INST_IO")))
    new_clone
  }

  val ports = port_map map { case (n, d) => d match {
    case v: Vec =>
      n -> {
        val vec = v.clone(clone_fn _)
        vec.bind(v.binding)
        vec.bypass         = v.bypass
        vec.decl_name      = v.decl_name
        vec.suggested_name = v.suggested_name
        vec.setRef(InstanceIO(this, v.computeName(None, "INST_VEC_IO")))
        vec
      }
	  case a: Aggregate =>
      n -> {
        val agg = a.clone(clone_fn _)
        agg.bind(a.binding)
        agg.bypass         = a.bypass
        agg.decl_name      = a.decl_name
        agg.suggested_name = a.suggested_name
        agg.setRef(InstanceIO(this, a.computeName(None, "INST_AGG_IO")))
        agg
      }
	  case b: Bits =>
      n -> b.clone(clone_fn _)
    }
  }

  def apply(port: String): Data = {
    val p_map = ports.toMap
    val p = p_map(port)
    p.bypass = false
    p.used = true
    p
  }
}
