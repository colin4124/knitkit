package example

import knitkit._

class PacketCase extends RawModule {
  def packet_tx = Agg(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ).prefix("tx")

  def packet_rx = Agg(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  )).prefix("rx")

  def packet = Agg(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ))

  val io = IO(Agg(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ), "")

  val reg = Wire(packet)
  reg <> io("inPacket")
  reg <> io("outPacket")
}
