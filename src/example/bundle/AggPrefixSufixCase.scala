package example

import knitkit._

class AggPrefixSufixCase extends RawModule {
  def packet_data = Agg(
    "data"   -> UInt(32.W).suggestName("car"),
  )
  def packet_valid = Agg(
    "valid"   -> UInt(32.W),
  )

  def packet_tx = packet_data.prefix(Seq("tx", "rx"))

  def packet_rx = packet_valid.suffix(Seq("tx", "rx"))

  val io = IO(Agg(
    "inPacket"  -> Input (packet_tx),
    "outPacket" -> Output(packet_rx),
  ), "")

  io("outPacket")("valid_tx") := io("inPacket")("tx_data")
  io("outPacket")("valid_rx") := io("inPacket")("rx_data")
}


