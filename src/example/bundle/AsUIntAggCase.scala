package example

import knitkit._

class AsUIntAggCase extends RawModule {
  def bar = Agg (
    "c" -> UInt(1.W),
    "d" -> UInt(20.W),
  )

  def foo = Agg (
  "a"   -> UInt(10.W),
  "bar" -> Vec(2, bar),
  "b"   -> UInt(11.W),
  )

  val in  = IO(Input(foo))
  val out = IO(Output(UInt(63.W)))

  out := in.asUInt
}

