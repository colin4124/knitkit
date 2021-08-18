package knitkit

import internal._
import ir._

trait Enum {
  protected def createValues(n: Int, named: Seq[String]): Seq[Bits] = {
    if (named.nonEmpty)
      require(named.size == n, "Enum named size must be the same!")
    val values = (0 until n).map(_.U((1 max log2Ceil(n)).W))
    values.zipWithIndex map { case(v, idx) =>
      v.getRef match {
        case l: Literal =>
          val e = {
            if (named.nonEmpty) UInt(v.width).suggestName(named(idx))
            else  UInt(v.width)
          }
          e.bind(EnumBinding(Builder.forcedUserModule, l))
          e
        case _ =>
          Builder.error("Literal!")
      }
    }
  }

  /** Returns n unique UInt values
    *
    * @param n Number of unique UInt constants to enumerate
    * @return Enumerated constants
    */
  def apply(n: Int, named: Seq[String] = Seq()): List[Bits] = createValues(n, named).toList
}

object Enum extends Enum
