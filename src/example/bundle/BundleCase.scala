package example

import knitkit._

class BundleCase extends RawModule {
  val io = IO(Agg(
    "sign"     -> Output(Bool()).suggestName("foo"),
    "exponent" -> Agg(
      "1" -> Agg(
        "a" -> Output(UInt(8.W)),
        "b" -> Output(UInt(8.W)),
      ),
      "2" -> Output(UInt( 8.W)),
    ).suggestName("bar"),
    "significand" -> Output(UInt(23.W)),
  ))

  val my_float = Agg(
   "sign"        -> Bool(),
   "exponent"    -> UInt(8.W),
   "significand" -> UInt(23.W),
  )
  // Floating point constant.
  val floatConst = Wire(my_float)
  floatConst("sign"       ) := true.B
  floatConst("exponent"   ) := 10.U
  floatConst("significand") := 128.U

  io("sign"       )      := floatConst("sign")
  io("exponent"   )("1")("a") := floatConst("exponent")
  io("exponent"   )("1")("b") := floatConst("exponent")
  io("exponent"   )("2") := floatConst("exponent")
  io("significand")      := floatConst("significand")
}
