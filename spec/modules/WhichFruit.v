module WhichFruit (
  input        clk,
  input        rst,
  input        sel,
  input        apple,
  input        cherry,
  input  [2:0] water,
  output [2:0] juice
);
  wire fruit;
  reg  [2:0] bowl;
  assign juice = bowl;
  assign fruit = (sel & apple) | ((~ sel) & cherry);
  always @(posedge clk) begin
    if (rst) begin
      bowl <= 3'h5;
    end
    else begin
      bowl <= bowl + fruit;
    end
  end
endmodule
