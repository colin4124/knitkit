package example

import knitkit._

class DecoupledIOCase extends RawModule {
  def info = Bundle(
    "data" -> UInt(8.W),
  )

  val io = IO(DecoupledIO(info), "")

  io("valid") := 1.B
  io("bits")("data") := 11.U
}
