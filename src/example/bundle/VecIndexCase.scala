package example

import knitkit._

class VecIndexCase extends RawModule {
  val addr   = IO(Input(UInt(2.W)))
  val result = IO(Output(UInt(6.W)))

  val func = IO(Agg(
    "a" -> Input(UInt(6.W)),
    "b" -> Input(UInt(6.W)),
    "c" -> Input(UInt(6.W)),
  ))

  val func_vec = Seq(func("a"), func("b"), func("c"))

  VecIndex(result, func_vec, addr)
}
