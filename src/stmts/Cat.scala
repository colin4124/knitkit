package knitkit

import math.pow

import ir._
import internal.Builder.{pushOp, error}

object Cat {
  def apply[T <: Bits](a: T, r: T*): Bits = apply(a :: r.toList)

  def apply[T <: Bits](r: Seq[T]): Bits = r match {
    case b: Seq[Bits] =>
      val w = b.foldLeft(IntWidth(0)) {  (res, d) => res + d.width }
      val args = b map { _.ref }
      pushOp(UInt(w), PrimOps.CatOp, args: _*)
    case _ =>
      error("Not support")
  }
}

object CatGroup {
  def apply[T <: Bits](r: Seq[T], group_num: Int = 0, prefix: String = "CAT"): Bits = r match {
    case b: Seq[Bits] =>
      require(group_num > 1, s"${group_num} must > 1")
      group_cat(r, group_num, prefix, r.size - 1, 0, 0)
    case _ =>
      error("Not support")
  }
  def group_cat(r: Seq[Bits], num: Int, prefix: String, msb: Int, lsb: Int, level: Int): Bits = {
    if (num < r.size) {
      var cur_msb = msb
      val grouped = r.grouped(num).toList
      val idx_list = (0 until grouped.size)
      val cur_cat = (idx_list zip grouped) map { case (idx, sub) =>
        // println(idx)
        // println(sub)
        val gap = pow(num, level+1).toInt
        val child_msb = msb - gap * idx
        val pending_lsb = child_msb - gap + 1
        val child_lsb = if (pending_lsb < 0) 0 else pending_lsb
        // println(s"${child_msb}:${child_lsb}")
        if (sub.size == 1) {
          sub(0)
        } else {
          val child = group_cat(sub, num, prefix, child_msb, child_lsb, level)
          WireInit(child).suggestName(s"${prefix}_${child_msb}_${child_lsb}")
        }
      }
      val result = group_cat(cur_cat, num, prefix, msb, lsb, level+1)
      if (level < 2) {
        result
      } else {
        WireInit(result).suggestName(s"${prefix}_${msb}_${lsb}")
      }
    } else if (r.size == 1) {
      r(0)
    } else {
      Cat(r)
    }
  }
}
