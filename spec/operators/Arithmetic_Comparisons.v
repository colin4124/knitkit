module Arithmetic_Comparisons (
  input  [7:0] a,
  input  [7:0] b,
  output       gt,
  output       gte,
  output       lt,
  output       lte
);
  assign gt = a > b;
  assign gte = a >= b;
  assign lt = a < b;
  assign lte = a <= b;
endmodule
