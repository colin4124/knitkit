module VecCase (
  output        io_sign,
  output [7:0]  io_exponent,
  output [22:0] io_significand,
  output [15:0] io_foo_0,
  output [15:0] io_foo_1,
  output [15:0] io_foo_2
);
  wire floatConst_sign;
  wire [7:0] floatConst_exponent;
  wire [22:0] floatConst_significand;
  wire [15:0] floatConst_foo_0;
  wire [15:0] floatConst_foo_1;
  wire [15:0] floatConst_foo_2;
  assign io_sign = floatConst_sign;
  assign io_exponent = floatConst_exponent;
  assign io_significand = floatConst_significand;
  assign io_foo_0 = floatConst_foo_0;
  assign io_foo_1 = floatConst_foo_1;
  assign io_foo_2 = floatConst_foo_2;
  assign floatConst_sign = 1'h1;
  assign floatConst_exponent = 8'ha;
  assign floatConst_significand = 23'h80;
  assign floatConst_foo_0 = 16'h16;
  assign floatConst_foo_1 = 16'h21;
  assign floatConst_foo_2 = 16'h2c;
endmodule
