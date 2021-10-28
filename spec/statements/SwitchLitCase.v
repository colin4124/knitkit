module SwitchLitCase (
  input        clk,
  input        rst,
  input        in,
  output [1:0] out,
  output [2:0] out_num
);
  reg  [2:0] reg;
  reg  state;
  assign out_num = reg;
  always @(posedge clk) begin
    if (rst) begin
      state <= 1'h0;
    end
    else begin
      state <= in;
    end
  end
  always @* begin
    case (state)
      1'h0: begin
        out => 2'h1;
      end
      1'h1: begin
        out => 2'h3;
      end
      default: begin
        out => 2'h0;
      end
    endcase //state
  end
  always @(posedge clk) begin
    case (state)
      1'h0: begin
        reg => 3'h2;
      end
      1'h1: begin
        reg => 3'h4;
      end
      default: begin
        reg => 3'h0;
      end
    endcase //state
  end
endmodule
