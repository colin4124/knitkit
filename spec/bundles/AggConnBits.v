module AggConnBits (
  output [15:0] io_header,
  output [15:0] io_addr,
  output [31:0] io_data
);
  wire [15:0] inner_header;
  wire [15:0] inner_addr;
  wire [31:0] inner_data;
  assign io_header = inner_header;
  assign io_addr = inner_addr;
  assign io_data = inner_data;
  assign inner_header = 32'h0;
  assign inner_addr = 32'h0;
  assign inner_data = 32'h0;
endmodule
