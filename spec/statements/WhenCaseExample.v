module WhenCaseExample (
  input        clk,
  input        sel1,
  input        sel2,
  output [3:0] out1,
  output [3:0] out2
);
  reg  [3:0]  foo;
  reg  [3:0]  bar;

  assign out1 = foo;
  assign out2 = bar;

  always @* begin
    if (sel1) begin
      foo <= 4'h5;
    end
    else if (sel2) begin
      foo <= 4'hf;
    end
    else begin
      foo <= 4'h2;
    end
  end

  always @(posedge clk) begin
    if (sel1) begin
      bar <= 4'h3;
    end
    else if (sel2) begin
      bar <= 4'hd;
    end
    else begin
      bar <= 4'h4;
    end
  end
endmodule
