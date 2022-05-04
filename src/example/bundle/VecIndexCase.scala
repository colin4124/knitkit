package example

import knitkit._

class VecIndexCase extends RawModule {
  val addr   = IO(Input(UInt(2.W)))
  val result = IO(Output(UInt(6.W)))

  val func = IO(Vec(3, Input(UInt(6.W))))

  result := func(addr)
}
