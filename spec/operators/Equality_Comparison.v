module Equality_Comparison (
  input  [4:0] x,
  input  [3:0] y,
  output       equ,
  output       neq
);
  assign equ = x == y;
  assign neq = x != y;
endmodule
