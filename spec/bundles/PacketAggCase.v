module PacketAggCase (
  input  [15:0] inPacket_tx_header,
  input  [15:0] inPacket_tx_addr,
  input  [31:0] inPacket_tx_data,
  output [15:0] outPacket_rx_header,
  output [15:0] outPacket_rx_addr,
  output [31:0] outPacket_rx_data
);
  wire [15:0]  reg_header;
  wire [15:0]  reg_addr;
  wire [31:0]  reg_data;

  assign outPacket_rx_header = reg_header;
  assign outPacket_rx_addr = reg_addr;
  assign outPacket_rx_data = reg_data;
  assign reg_header = inPacket_tx_header;
  assign reg_addr = inPacket_tx_addr;
  assign reg_data = inPacket_tx_data;
endmodule
