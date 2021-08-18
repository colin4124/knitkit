package knitkit

import collection.mutable.HashMap

import internal._
import ir._

case class Instance(port_map: Map[String, Data]) extends HasId {
  def clone_fn(clone: Bits, orig: Bits): Bits = {
    clone.bind(orig.binding)
    clone.suggested_name = orig.suggested_name
    clone.direction = orig.direction
    clone.setRef(InstanceIO(this, orig.suggestedName.get))
    clone
  }

  val agg_ports = HashMap[String, Aggregate]()
  val bit_ports = HashMap[String, Bits]()

  val ports = port_map map { case (n, d) => d match {
	  case a: Aggregate =>
      n -> a.clone(clone_fn _)
	  case b: Bits =>
      n -> b.clone(clone_fn _)
    }
  }

  def apply(port: String): Data = {
    ports(port)
  }
}
