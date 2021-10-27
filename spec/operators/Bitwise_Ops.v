module Bitwise_Ops (
  input  [31:0] x,
  output [31:0] invertedX,
  output [31:0] hiBits,
  output [31:0] OROut,
  output [31:0] XOROut
);
  assign invertedX = ~ x;
  assign hiBits = x & 32'hffff0000;
  assign OROut = invertedX | hiBits;
  assign XOROut = invertedX ^ hiBits;
endmodule
