package example

import knitkit._

class AsUIntGroupBundleCase extends RawModule {
  def bar = Bundle (
    "c" -> UInt(1.W),
    "d" -> UInt(20.W),
  )

  def foo = Bundle (
  "a"   -> UInt(10.W),
  "bar" -> Vec(2, bar),
  "b"   -> UInt(11.W),
  )

  val in  = IO(Input(foo))
  val out = IO(Output(UInt(63.W)))

  out := in.asUIntGroup(2, "in")
}

