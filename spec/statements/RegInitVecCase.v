module RegInitVecCase (
  input        clk,
  input        rst,
  input  [2:0] foo_0,
  input  [2:0] foo_1,
  input  [2:0] foo_2,
  output [2:0] foo_out_0,
  output [2:0] foo_out_1,
  output [2:0] foo_out_2
);
  reg  [2:0] reg_foo_0;
  reg  [2:0] reg_foo_1;
  reg  [2:0] reg_foo_2;
  assign foo_out_0 = reg_foo_0;
  assign foo_out_1 = reg_foo_1;
  assign foo_out_2 = reg_foo_2;
  always @(posedge clk) begin
    if (rst) begin
      reg_foo_0 <= 3'h0;
    end
    else begin
      reg_foo_0 <= foo_0;
    end
  end
  always @(posedge clk) begin
    if (rst) begin
      reg_foo_1 <= 3'h0;
    end
    else begin
      reg_foo_1 <= foo_1;
    end
  end
  always @(posedge clk) begin
    if (rst) begin
      reg_foo_2 <= 3'h0;
    end
    else begin
      reg_foo_2 <= foo_2;
    end
  end
endmodule
