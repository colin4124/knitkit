package knitkit

import ir._
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
    clone.decl_name = orig.decl_name
    clone.bypass    = orig.bypass
    clone.suggested_name = orig.suggested_name
    clone
  }

  def clone_fn_all(clone: Data, orig: Data): Data = {
    val new_clone = clone_fn_base(clone, orig)
    new_clone.bind(orig.binding)
    new_clone
  }

  def sortedIDs[T <: HasId, S](id_map: HashMap[T, S]): Seq[(T, S)] = {
    id_map.toSeq.sortBy { case (id, _) => id._id }
  }

  def padToMax(strs: Seq[String]): Seq[String] = {
    val len = if (strs.nonEmpty) strs.map(_.length).max else 0
    strs map (_.padTo(len, ' '))
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
