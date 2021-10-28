module RegInitDoubleArgCase (
  input        clk,
  input        rst,
  input  [2:0] x_init,
  input  [7:0] y_init,
  input  [2:0] x_in,
  input  [7:0] y_in,
  output [2:0] x_out,
  output [7:0] y_out
);
  wire [2:0] x;
  wire [7:0] y;
  reg  [2:0] r1;
  reg  [7:0] r2;
  assign x_out = r1;
  assign y_out = r2;
  assign x = x_init;
  assign y = y_init;
  always @(posedge clk) begin
    if (rst) begin
      r1 <= x;
    end
    else begin
      r1 <= x_in;
    end
  end
  always @(posedge clk) begin
    if (rst) begin
      r2 <= y;
    end
    else begin
      r2 <= y_in;
    end
  end
endmodule
