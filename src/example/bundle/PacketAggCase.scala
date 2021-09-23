package example

import knitkit._

class PacketAggCase extends RawModule {
  def packet = Agg(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ))

  def packet_tx = packet.prefix("tx")

  def packet_rx = packet.prefix("rx")

  val io = IO(Agg(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ), "")

  val reg = Wire(packet)
  reg <> io("inPacket")
  reg <> io("outPacket")
}
