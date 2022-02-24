module RegCase (
  input        clk,
  input        clk_neg,
  input  [2:0] in,
  output [2:0] out,
  output [2:0] out_neg
);
  reg  [2:0]  delayReg;
  reg  [2:0]  negReg;

  assign out = delayReg;
  assign out_neg = negReg;

  always @(posedge clk) begin
    delayReg <= in;
  end

  always @(negedge clk_neg) begin
    negReg <= in;
  end
endmodule
