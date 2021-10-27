module SpecifiedWidth (
  output [3:0]  out1,
  output [31:0] out2,
  output [6:0]  out3,
  output [7:0]  out4
);
  assign out1 = 4'h8;
  assign out2 = -32'sh98;
  assign out3 = 7'sh5;
  assign out4 = 8'h5;
endmodule
