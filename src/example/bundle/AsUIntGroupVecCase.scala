package example

import knitkit._

class AsUIntGroupVecCase extends RawModule {
  def bar = Bundle(
    "c" -> UInt(1.W),
    "d" -> UInt(20.W),
  )

  def foo = Bundle(
  "a"   -> UInt(10.W),
  "bar" -> Vec(2, bar),
  "b"   -> UInt(11.W),
  )

  def car = Vec(2, foo)

  val in  = IO(Input(car))
  val out = IO(Output(UInt(126.W)))

  out := in.asUIntGroup(4, "in")
}
