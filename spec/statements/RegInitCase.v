module RegInitCase (
  input        clk_1,
  input        rst_1,
  input        clk_2,
  input        rst_2,
  input        clk_3,
  input        rst_3,
  input  [2:0] foo,
  input  [2:0] bar,
  input  [2:0] cat,
  output [2:0] foo_out,
  output [2:0] bar_out,
  output [2:0] cat_out
);
  reg  [2:0] reg_foo;
  reg  [2:0] reg_bar;
  reg  [2:0] reg_cat;
  assign foo_out = reg_foo;
  assign bar_out = reg_bar;
  assign cat_out = reg_cat;
  always @(posedge clk_1) begin
    if (rst_1) begin
      reg_foo <= 3'h0;
    end
    else begin
      reg_foo <= foo;
    end
  end
  always @(posedge clk_2 or negedge rst_2) begin
    if (!rst_2) begin
      reg_bar <= 3'h0;
    end
    else begin
      reg_bar <= bar;
    end
  end
  always @(posedge clk_3 or posedge rst_3) begin
    if (rst_3) begin
      reg_cat <= 3'h0;
    end
    else begin
      reg_cat <= cat;
    end
  end
endmodule
