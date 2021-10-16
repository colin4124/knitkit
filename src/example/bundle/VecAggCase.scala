package example

import knitkit._

class VecAggCase extends RawModule {
  def packet = Agg(Seq(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  ))

  def packet_tx = Vec(3, packet.prefix("tx"))
  def packet_rx = Vec(4, packet.prefix("rx"))

  val io = IO(Agg(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ))
}
