module RegVecCase (
  input        clk,
  input  [2:0] in_0,
  input  [2:0] in_1,
  input  [2:0] in_2,
  output [2:0] out_0,
  output [2:0] out_1,
  output [2:0] out_2
);
  reg  [2:0]  delayReg_0;
  reg  [2:0]  delayReg_1;
  reg  [2:0]  delayReg_2;

  assign out_0 = delayReg_0;
  assign out_1 = delayReg_1;
  assign out_2 = delayReg_2;

  always @(posedge clk) begin
    delayReg_0 <= in_0;
  end

  always @(posedge clk) begin
    delayReg_1 <= in_1;
  end

  always @(posedge clk) begin
    delayReg_2 <= in_2;
  end
endmodule
