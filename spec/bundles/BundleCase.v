module BundleCase (
  output        io_foo,
  output [7:0]  io_bar_1_a,
  output [7:0]  io_bar_1_b,
  output [7:0]  io_bar_2,
  output [22:0] io_significand
);
  wire         floatConst_sign;
  wire [7:0]   floatConst_exponent;
  wire [22:0]  floatConst_significand;

  assign io_foo = floatConst_sign;
  assign io_bar_1_a = floatConst_exponent;
  assign io_bar_1_b = floatConst_exponent;
  assign io_bar_2 = floatConst_exponent;
  assign io_significand = floatConst_significand;
  assign floatConst_sign = 1'h1;
  assign floatConst_exponent = 8'ha;
  assign floatConst_significand = 23'h80;
endmodule
