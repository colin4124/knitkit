module AggPrefixSufixCase (
  input  [31:0] inPacket_tx_car,
  input  [31:0] inPacket_rx_car,
  output [31:0] outPacket_valid_tx,
  output [31:0] outPacket_valid_rx
);
  assign outPacket_valid_tx = inPacket_tx_car;
  assign outPacket_valid_rx = inPacket_rx_car;
endmodule
