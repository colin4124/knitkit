module ElseWhenCase (
  input        clk,
  input        rst,
  input        foo_sel1,
  input        foo_sel2,
  input        bar_sel1,
  input        bar_sel2,
  output [2:0] out1,
  output [2:0] out2
);
  reg  [2:0]  foo;
  reg  [2:0]  bar;

  assign out1 = foo;
  assign out2 = bar;

  always @* begin
    if (foo_sel1) begin
      foo <= 3'h5;
    end
    else if (foo_sel2) begin
      foo <= 3'h4;
    end
    else begin
      foo <= 3'h0;
    end
  end

  always @(posedge clk) begin
    if (rst) begin
      bar <= 3'h0;
    end
    else if (bar_sel1) begin
      bar <= 3'h3;
    end
    else if (bar_sel2) begin
      bar <= 3'h2;
    end
    else begin
      bar <= 3'h0;
    end
  end
endmodule
