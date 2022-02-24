module OtherwiseCase (
  input        clk,
  input        rst,
  input        sel1,
  input        sel2,
  output [2:0] out1,
  output [2:0] out2,
  output [3:0] out3
);
  reg  [2:0]  foo;
  reg  [2:0]  bar;
  reg  [3:0]  car;

  assign out1 = foo;
  assign out2 = bar;
  assign out3 = car;

  always @* begin
    if (sel1) begin
      foo <= 3'h5;
      if (sel2) begin
        foo <= 3'h7;
      end
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

  always @(posedge clk or negedge rst) begin
    if (!rst) begin
      car <= 4'he;
    end
    else if (sel2) begin
      car <= 4'h2;
    end
    else if (sel1) begin
      car <= 4'h3;
    end
    else begin
      car <= 4'h1;
    end
  end
endmodule
