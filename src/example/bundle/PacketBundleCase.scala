package example

import knitkit._

class PacketBundleCase extends RawModule {
  def packet = Bundle(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ))

  def packet_tx = packet.prefix("tx")

  def packet_rx = packet.prefix("rx")

  val io = IO(Bundle(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ), "")

  val reg = Wire(packet)
  reg <> io("inPacket")
  reg <> io("outPacket")
}
