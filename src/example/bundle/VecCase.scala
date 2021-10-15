package example

import knitkit._

class VecCase extends RawModule {
  val io = IO(Vec(
    Output(Bool(), "sign"),
    Output(UInt(8.W), "exponent"),
    Output(UInt(23.W), "significand"),
    Output(Vec(3, UInt(16.W)), "foo"),
  ))

  val my_float = Vec(
    "sign"     -> Bool(),
    "exponent" -> UInt(8.W),
    "significand" -> UInt(23.W),
    "foo" -> Vec(3, UInt(16.W))
  )
  // Floating point constant.
  val floatConst = Wire(my_float)
  floatConst(0) := true.B
  floatConst(1) := 10.U
  floatConst(2) := 128.U
  floatConst(3)(0) := 22.U
  floatConst(3)(1) := 33.U
  floatConst(3)(2) := 44.U

  io <> floatConst
}
