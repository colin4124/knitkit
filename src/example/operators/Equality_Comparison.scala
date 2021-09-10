package example

import knitkit._

class Equality_Comparison extends RawModule {
  val x = IO(Input(UInt(5.W)))
  val y = IO(Input(UInt(4.W)))
  val equ = IO(Output(Bool()))
  val neq = IO(Output(Bool()))

  equ := x === y
  neq := x =/= y
}
