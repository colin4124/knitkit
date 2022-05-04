package example

import knitkit._

class BundleDontCareCase extends RawModule {
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

  val floatConst = Wire(my_float)
  floatConst <> DontCare

  io <> DontCare
}
