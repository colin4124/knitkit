module BundleDontCareCase (
  output        io_foo,
  output [7:0]  io_bar_1_a,
  output [7:0]  io_bar_1_b,
  output [7:0]  io_bar_2,
  output [22:0] io_significand
);
  wire         floatConst_sign;
  wire [7:0]   floatConst_exponent;
  wire [22:0]  floatConst_significand;

  assign io_foo = 1'h0;
  assign io_bar_1_a = 1'h0;
  assign io_bar_1_b = 1'h0;
  assign io_bar_2 = 1'h0;
  assign io_significand = 1'h0;
  assign floatConst_sign = 1'h0;
  assign floatConst_exponent = 1'h0;
  assign floatConst_significand = 1'h0;
endmodule
