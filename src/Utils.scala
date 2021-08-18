package knitkit

import ir._

object Utils {
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
      case (_, _) => false
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
