module Logical_Operations (
  input  sel,
  input  a,
  input  b,
  output sleep,
  output hit,
  output stall,
  output out
);
  assign sleep = a == 1'h0;
  assign hit = a & b;
  assign stall = a | b;
  assign out = sel ? a : b;
endmodule
