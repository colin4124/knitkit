module RegNextCase (
  input        clk,
  input  [2:0] in,
  output [2:0] out
);
  reg  [2:0]  delayReg;

  assign out = delayReg;

  always @(posedge clk) begin
    delayReg <= in;
  end
endmodule
