package knitkit

import internal._
import internal.Builder.error
import Utils._

class Vec(eles: Seq[Data]) extends Data with VecOps {
  val elements: Seq[Data] = eles

  val duplicates = getElements.groupBy(identity).collect { case (x, elts) if elts.size > 1 => x }
  if (!duplicates.isEmpty) {
    throw new AliasedBundleFieldException(s"Bundle $this contains aliased fields $duplicates")
  }

  def apply(name: String): Data = error(s"Vec Not Support string extract")

  def apply(addr_raw: Data): WhenCase = {
    val addr = addr_raw.asBits
    val addr_w = addr.getWidth
    val addr_max = math.pow(2, addr_w.toInt).toInt
    val vec_size = elements.size
    require(vec_size > 0)
    require(addr_max >= vec_size)

    if (vec_size == 1) {
      WhenCase(elements(0), Seq())
    } else {
      val when_cond = (1 until vec_size) map { idx =>
        (addr === idx.U) -> elements(idx)
      }

      WhenCase(elements(0), when_cond)
    }
  }

  def reduce[B >: Data](op: (B, B) => B): B = elements.reduce(op)
  def map[B](op: Data => B): Seq[B] = elements.map(op)
  def zipWithIndex = elements.zipWithIndex
  def foreach[B](op: Data => Unit) = elements.foreach(op)

  def litOption: Option[BigInt] = None

  def getPair: Seq[(String, Data)] = error(s"Vec Not Support get pair")
  def getDir: SpecifiedDirection = error(s"Vec Not Support get direction")

  def apply(idx: Int*): Data = {
    require(idx.size == 1)
    val e = elements(idx(0))
    e.used = true
    e
  }



  def get_ele(idx: Int*): Data = {
    val e = elements(idx(0))
    if(idx.size == 1) {
      e
    } else {
      e match {
	      case v: Vec => v.get_ele(idx.drop(1): _*)
        case _ => Builder.error(s"TODO")
      }
    }
  }

  def _onModuleClose: Unit = {
    for (elt <- elements) {
      elt._parentID = Some(this)
      // elt.setRef(this, elt.computeName(None, ""))
    }
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

  def flattenElements: Seq[Bits] = {
    getElements flatMap {
      case a: Bundle => a.flattenElements
      case v: Vec    => v.flattenElements
      case b: Bits   => Seq(b)
    }
  }

  def reversedVecElements: Seq[Bits] = {
    getElements.reverse flatMap {
      case a: Bundle => a.reversedVecElements
      case v: Vec    => v.reversedVecElements
      case b: Bits   => Seq(b)
    }
  }

  def bind(target: Binding): Unit = {
    binding = target
  }

  def clone(fn: (Data, Data) => Data = (x, y) => x): Vec = {
    val vec = new Vec(elements map { data =>
      val clone_data = data match {
        case b: Bits   => b.clone(fn)
        case a: Bundle => a.clone(fn)
        case v: Vec    => v.clone(fn)
      }
      clone_data
    })
    fn(vec, this) match {
      case v: Vec => v
      case _ => Builder.error("Vec clone should be vec")
    }
  }

  def asUInt: Bits = {
    Cat(reversedVecElements map { _.asUInt })
  }

  def asUIntGroup(group_num: Int = 0, prefix: String = "CAT"): Bits = {
    val eles = reversedVecElements map { _.asUInt }
    CatGroup(eles, group_num, prefix)
  }
}

object Vec {
  def clone_fn(clone: Data, orig: Data): Data = {
    val new_clone = clone_fn_all(clone, orig)
    Builder.currentModule match {
      case Some(m: RawModule) =>
        val cur_regs_info = m._regs_info
        (new_clone, orig) match {
          case (c: Bits, o: Bits) =>
            if (cur_regs_info.contains(o)) {
              cur_regs_info(c) = cur_regs_info(o)
            }
          case _ =>
        }
      case _ =>
    }
    new_clone
  }

  def bind(v: Vec): Vec = {
    // check elements' binding must be the same
    (v.getElements zip v.getElements.drop(1)) foreach { case (a, b) =>
      if (!sameBinding(a, b)) {
        error(s"Vec elements' binding must be the same $a $b")
      }
    }
    v._binding = v.getElements(0)._binding
    v
  }

  def apply(total: Int, ele: Data, offset: Int = 0): Vec = {
    // requireIsknitkitType(ele) // TODO
    val begin_idx = offset
    val end_idx   = total + offset
    val eles = (begin_idx until end_idx) map { idx =>
      val clone_data: Data = ele match {
        case b: Bits   => b.clone(clone_fn)
        case a: Bundle => a.clone(clone_fn)
        case v: Vec    => v.clone(clone_fn)
        case _ => ele
      }
      clone_data.suffix(s"$idx")
    }
    // Remove unsed orignal model data's binding
    ele match {
	    case b: Bits =>
        Builder.currentModule match {
          case Some(m: RawModule) =>
            m._regs_info -= b
          case _ =>
        }
      case _ =>
    }
    ele._binding = None
    bind(new Vec(eles))
  }
  def apply(a: Data, r: Data*): Vec = apply(a :: r.toList)
  def apply(your_eles: Seq[Data]): Vec = {
    // your_eles foreach { requireIsknitkitType(_) } // TODO
    val named_eles = your_eles.zipWithIndex map { case (d, i) => d.suggestName(s"$i", alter = false) }
    bind(new Vec(named_eles))
  }

  def apply(a: (String, Data), r: (String, Data)*): Vec = {
    val your_eles = a :: r.toList
    val named_eles = your_eles map { case (n, d) => d.suggestName(n, alter = false) }
    bind(new Vec(named_eles))
  }
}

// object VecIndex {
//   def apply(dest: Data, v: Seq[Data], addr_raw: Data): Unit = {
//     val addr = addr_raw.asBits
//     val addr_w = addr.getWidth
//     val addr_max = math.pow(2, addr_w.toInt).toInt
//     val vec_size = v.size
//     require(vec_size > 0)
//     require(addr_max >= vec_size)

//     if (vec_size == 1) {
//       dest := v(0)
//     } else {
//       val when_cond = (1 until vec_size) map { idx =>
//         (addr === idx.U) -> v(idx)
//       }

//       WhenCase(dest, v(0), when_cond)
//     }
//   }
// }

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
  def asClockNeg = not_def_op
  def asReset = not_def_op
  def asAsyncPosReset = not_def_op
  def asAsyncNegReset = not_def_op
}
