module Bitwise_Reductions (
  input  [31:0] x,
  output        allSet,
  output        anySet,
  output        parity
);
  assign allSet = &x;
  assign anySet = |x;
  assign parity = ^x;
endmodule
