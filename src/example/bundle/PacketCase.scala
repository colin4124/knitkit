package example

import knitkit._

class PacketCase extends RawModule {
  def packet = Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  )
  val io = IO(Agg(
    "inPacket"  -> Input (Agg(packet)),
    "outPacket" -> Output(Agg(packet)),
  ))

  val reg = Wire(Agg(packet))
  reg <> io("inPacket")
  reg <> io("outPacket")

  io("inPacket") <> io("outPacket")
}
