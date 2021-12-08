package knitkit

import ir._
import internal._
import internal.Builder.error
import collection.mutable.HashMap

object Utils {
  def clone_fn_base(clone: Data, orig: Data): Data = {
    (clone, orig) match {
      case (l: Bits, r: Bits) =>
        l._prefix ++= r._prefix
        l._suffix ++= r._suffix
        l.direction = r.direction
      case _ =>
    }
    clone match {
      case a: Aggregate => a._onModuleClose
      case v: Vec       => v._onModuleClose
      case _ =>
    }
    clone.decl_name = orig.decl_name
    clone.bypass    = orig.bypass
    clone.suggested_name = orig.suggested_name
    clone
  }

  def clone_fn_all(clone: Data, orig: Data): Data = {
    val new_clone = clone_fn_base(clone, orig)
    if (orig._binding.nonEmpty) {
      new_clone.bind(orig.binding)
    }
    new_clone
  }

  def sortedIDs[T <: HasId, S](id_map: HashMap[T, S]): Seq[(T, S)] = {
    id_map.toSeq.sortBy { case (id, _) => id._id }
  }

  def getId(expr: Expression): Long = {
     expr match {
       case n: Node => n.id._id
       case _ => error("must be Node")
     }
  }

  def wrapClkInfo(info: ClkInfo): ClkInfoIdx = {
    info match {
      case a @ ClkInfo(Some(_), Some(rst_val)) =>
        ClkInfoIdx(getId(rst_val), a)
      case b @ ClkInfo(Some(clk), None) =>
        ClkInfoIdx(getId(clk), b)
      case other =>
        ClkInfoIdx(-1, other)
    }
  }

  def padToMax(strs: Seq[String]): Seq[String] = {
    val len = if (strs.nonEmpty) strs.map(_.length).max else 0
    val has_signed = strs map (_.contains("signed")) reduce(_ || _)
    if (has_signed) {
      strs map { s =>
        if (s.contains("signed")) {
          s.padTo(len, ' ')
        } else {
          val sz = "signed".size + 1
          " " * sz + s.padTo(len-sz, ' ')
        }
      }
    } else {
      strs map (_.padTo(len, ' '))
    }
  }

  def bypass_cvt_type(e: Expression, tpe: CvtType): Expression = {
    e match {
      case n: Node      => n.copy(cvt_type = tpe)
      case r: Reference => r.copy(cvt_type = tpe)
      case other => other
    }
  }

  def bypass_cvt_type(e: Expression, copy_e: Expression): Expression = {
    val tpe = copy_e match {
      case c: HasCvtType => c.cvt_type
      case _ => DontCvtType
    }
    e match {
      case n: Node      => n.copy(cvt_type = tpe)
      case r: Reference => r.copy(cvt_type = tpe)
      case other => other
    }
  }

  def sameType(a: Bits, b: Bits): Boolean = {
    (a.tpe, b.tpe) match {
      case (UIntType(_), UIntType(_)) => true
      case (SIntType(_), SIntType(_)) => true
      case (ClockType  , ClockType)   => true
      case (SyncResetType    , SyncResetType    ) => true
      case (AsyncNegResetType, AsyncNegResetType) => true
      case (AsyncPosResetType, AsyncPosResetType) => true
      case (AnalogType(_), AnalogType(_)) => true
      case (_, _) => false
    }
  }

  def sameBinding(a: Data, b: Data): Boolean = {
    (a._binding, b._binding) match {
      case (Some(OpAssignBinding(_)), Some(OpAssignBinding(_)))   => true
      case (Some(OpBinding      (_)), Some(OpBinding      (_)))   => true
      case (Some(PortBinding    (_)), Some(PortBinding    (_)))   => true
      case (Some(RegBinding     (_)), Some(RegBinding     (_)))   => true
      case (Some(WireBinding    (_)), Some(WireBinding    (_)))   => true
      case (Some(EnumBinding    (_, _)), Some(EnumBinding(_, _))) => true
      case (Some(LitBinding), Some(LitBinding)) => true
      case (None, None) => true
      case (other_a, other_b) =>
        error(s"Vec elements' binding must be the same $other_a $other_b")
        false
    }
  }

  def isUInt(a: Bits): Boolean = {
    a.tpe match {
      case (UIntType(_)) => true
      case _ => false
    }
  }

  def isResetType(a: Bits): Boolean = a.tpe match {
    case _: ResetType => true
    case _            => false
  }

  def isClockType(a: Bits): Boolean = a.tpe match {
    case ClockType    => true
    case ClockNegType => true
    case _            => false
  }

  def time[R](block: => R): (Double, R) = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    val timeMillis = (t1 - t0) / 1000000.0
    (timeMillis, result)
  }
}

object log2Ceil {
  def apply(in: BigInt): Int = {
    require(in > 0)
           (in-1).bitLength
  }
  def apply(in: Int): Int = apply(BigInt(in))
}

/** Casts BigInt to Int, issuing an error when the input isn't representable. */
object castToInt {
  def apply(x: BigInt, msg: String): Int = {
    val res = x.toInt
    require(x == res, s"$msg $x is too large to be represented as Int")
    res
  }
}
