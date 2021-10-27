module InferredWidth (
  output [4:0] out1,
  output [2:0] out2,
  output [3:0] out3,
  output [3:0] out4
);
  assign out1 = 5'h1;
  assign out2 = 3'h5;
  assign out3 = 4'sh5;
  assign out4 = -4'sh8;
endmodule
