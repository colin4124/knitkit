module Mux2 (
  input  sel,
  input  in0,
  input  in1,
  output out
);
  assign out = (sel & in1) | ((~ sel) & in0);
endmodule
