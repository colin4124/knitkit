package knitkit

import ir._
import internal._
import internal.Builder._

trait DataOps { this: Data =>
  def +  (that: Data): Bits
  def +& (that: Data): Bits
  def -  (that: Data): Bits
  def &  (that: Data): Bits
  def |  (that: Data): Bits
  def ^  (that: Data): Bits
  def /  (that: Data): Bits
  def %  (that: Data): Bits

  def <   (that: Data): Bits
  def >   (that: Data): Bits
  def <=  (that: Data): Bits
  def >=  (that: Data): Bits
  def =/= (that: Data): Bits
  def === (that: Data): Bits

  def || (that: Data): Bits
  def && (that: Data): Bits

  def << (that: Int ): Bits
  def << (that: Data): Bits

  def >> (that: Int ): Bits
  def >> (that: Data): Bits

  def asBool  : Bits
  def asClock : Bits
  def asReset : Bits
  def asAsyncPosReset : Bits
  def asAsyncNegReset : Bits
}
