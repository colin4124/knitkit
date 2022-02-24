module RegNextInitCase (
  input        clk,
  input        rst,
  input  [2:0] in,
  output [2:0] out
);
  reg  [2:0]  delayReg;

  assign out = delayReg;

  always @(posedge clk) begin
    if (rst) begin
      delayReg <= 3'h0;
    end
    else begin
      delayReg <= in;
    end
  end
endmodule
