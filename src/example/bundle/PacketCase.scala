package example

import knitkit._

class PacketCase extends RawModule {
  def packet_tx = Bundle(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ).prefix("tx")

  def packet_rx = Bundle(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  )).prefix("rx")

  def packet = Bundle(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ))

  val io = IO(Bundle(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ), "")

  val reg = Wire(packet)
  reg <> io("inPacket")
  reg <> io("outPacket")
}
