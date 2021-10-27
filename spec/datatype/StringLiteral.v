module StringLiteral (
  output [3:0]  out1,
  output [3:0]  out2,
  output [3:0]  out3,
  output [31:0] out4,
  output [7:0]  out5,
  output [5:0]  out6,
  output [11:0] out7
);
  assign out1 = 4'ha;
  assign out2 = 4'ha;
  assign out3 = 4'ha;
  assign out4 = 32'hdeadbeef;
  assign out5 = 8'ha;
  assign out6 = 6'ha;
  assign out7 = 12'ha;
endmodule
