module SwitchBundleCase (
  input        clk,
  input        rst,
  input        in,
  output reg [2:0] out_0,
  output reg [2:0] out_1,
  output reg [2:0] out_2
);
  reg  [2:0]  reg3_0;
  reg  [2:0]  reg3_1;
  reg  [2:0]  reg3_2;
  reg  [2:0]  reg4_0;
  reg  [2:0]  reg4_1;
  reg  [2:0]  reg4_2;
  reg         state;

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
        out_0 <= reg3_0;
        out_1 <= reg3_1;
        out_2 <= reg3_2;
      end
      1'h1: begin
        out_0 <= reg4_0;
        out_1 <= reg4_1;
        out_2 <= reg4_2;
      end
      default: begin
        out_0 <= 3'h0;
        out_1 <= 3'h0;
        out_2 <= 3'h0;
      end
    endcase //state
  end
endmodule
