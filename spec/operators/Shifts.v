module Shifts (
  input  [2:0]  x,
  input  [31:0] y,
  output [32:0] twoToTheX,
  output [15:0] hiBits
);
  assign twoToTheX = 2'sh1 << x;
  assign hiBits = y >> 5'h10;
endmodule
