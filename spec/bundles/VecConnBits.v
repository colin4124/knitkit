module VecConnBits (
  output [15:0] io_header_0,
  output [15:0] io_addr_0,
  output [31:0] io_data_0,
  output [15:0] io_header_1,
  output [15:0] io_addr_1,
  output [31:0] io_data_1
);
  wire [15:0] inner_header_0;
  wire [15:0] inner_addr_0;
  wire [31:0] inner_data_0;
  wire [15:0] inner_header_1;
  wire [15:0] inner_addr_1;
  wire [31:0] inner_data_1;
  assign io_header_0 = inner_header_0;
  assign io_addr_0 = inner_addr_0;
  assign io_data_0 = inner_data_0;
  assign io_header_1 = inner_header_1;
  assign io_addr_1 = inner_addr_1;
  assign io_data_1 = inner_data_1;
  assign inner_header_0 = 32'h0;
  assign inner_addr_0 = 32'h0;
  assign inner_data_0 = 32'h0;
  assign inner_header_1 = 32'h0;
  assign inner_addr_1 = 32'h0;
  assign inner_data_1 = 32'h0;
endmodule
