package example

import knitkit._

class BundleCase extends RawModule {
  val io = IO(Bundle(
    "sign"     -> Output(Bool()).suggestName("foo"),
    "exponent" -> Bundle(
      "1" -> Bundle(
        "a" -> Output(UInt(8.W)),
        "b" -> Output(UInt(8.W)),
      ),
      "2" -> Output(UInt( 8.W)),
    ).suggestName("bar"),
    "significand" -> Output(UInt(23.W)),
  ))

  val my_float = Bundle(
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
