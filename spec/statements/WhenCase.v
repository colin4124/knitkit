module WhenCase (
  input        clk,
  input        sel1,
  input        sel2,
  output [2:0] out1,
  output [2:0] out2
);
  reg  [2:0] foo;
  reg  [2:0] bar;
  assign out1 = foo;
  assign out2 = bar;
  always @* begin
    if (sel1) begin
      foo <= 3'h5;
    end
    else begin
      foo <= 3'h2;
    end
  end
  always @(posedge clk) begin
    if (sel2) begin
      bar <= 3'h3;
    end
    else begin
      bar <= 3'h4;
    end
  end
endmodule
