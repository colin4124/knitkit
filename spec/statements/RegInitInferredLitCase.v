module RegInitInferredLitCase (
  input        clk,
  input        rst,
  input  [2:0] x_in,
  input  [7:0] y_in,
  output [2:0] x_out,
  output [7:0] y_out
);
  reg  [2:0] x;
  reg  [7:0] y;
  assign x_out = x;
  assign y_out = y;
  always @(posedge clk) begin
    if (rst) begin
      x <= 3'h5;
    end
    else begin
      x <= x_in;
    end
  end
  always @(posedge clk) begin
    if (rst) begin
      y <= 8'h5;
    end
    else begin
      y <= y_in;
    end
  end
endmodule
