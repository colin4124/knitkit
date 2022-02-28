module VecIndexCase (
  input  [1:0] addr,
  output reg [5:0] result,
  input  [5:0] func_a,
  input  [5:0] func_b,
  input  [5:0] func_c
);
  always @* begin
    if (addr == 1'h1) begin
      result <= func_b;
    end
    else if (addr == 2'h2) begin
      result <= func_c;
    end
    else begin
      result <= func_a;
    end
  end
endmodule
