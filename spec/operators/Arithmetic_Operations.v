module Arithmetic_Operations (
  input  [7:0]  a,
  input  [7:0]  b,
  output [7:0]  sum,
  output [8:0]  diff0,
  output [8:0]  diff1,
  output [8:0]  diff2,
  output [15:0] prod,
  output [7:0]  div,
  output [7:0]  mod
);
  assign sum = a + b;
  assign diff0 = a - b;
  assign prod = a * b;
  assign div = a / b;
  assign mod = a % b;
endmodule
