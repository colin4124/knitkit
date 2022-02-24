module Bitfield_Manipulation (
  input  [3:0]  sel,
  input  [15:0] x,
  output        dynamicSel,
  output        xLSB,
  output [3:0]  xTopNibble,
  output [11:0] usDebt,
  output [16:0] float
);
  wire [15:0]  foo;

  assign dynamicSel = foo[0];
  assign xLSB = x[0];
  assign xTopNibble = x[15:12];
  assign usDebt = {4'ha, 4'ha, 4'ha};
  assign float = {xLSB, xTopNibble, usDebt};
  assign foo = x >> sel;
endmodule
