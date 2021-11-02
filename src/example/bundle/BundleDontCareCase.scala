package example

import knitkit._

class BundleDontCareCase extends RawModule {
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

  val floatConst = Wire(my_float)
  floatConst <> DontCare

  io <> DontCare
}
