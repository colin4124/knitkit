module Shifts (
  input         [2:0]  x,
  input         [31:0] y,
  input  signed [31:0] z,
  output signed [32:0] twoToTheX,
  output        [15:0] hiBits,
  output signed [15:0] sigHiBits,
  output        [15:0] usigHiBits
);
  wire signed [15:0] s_imm;
  wire [15:0] u_imm;
  assign twoToTheX = 2'sh1 << x;
  assign hiBits = y >> 5'h10;
  assign usigHiBits = u_imm;
  assign s_imm = $signed(y) >>> 5'h10;
  assign u_imm = $unsigned(z) >> 5'h10;
endmodule
