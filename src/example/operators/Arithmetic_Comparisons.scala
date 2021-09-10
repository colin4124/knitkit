package example

import knitkit._

class Arithmetic_Comparisons extends RawModule {
  val a = IO(Input(UInt(8.W)))
  val b = IO(Input(UInt(8.W)))
  val gt  = IO(Output(Bool()))
  val gte = IO(Output(Bool()))
  val lt  = IO(Output(Bool()))
  val lte = IO(Output(Bool()))

  gt  := a > b
  gte := a >= b
  lt  := a < b
  lte := a <= b
}
