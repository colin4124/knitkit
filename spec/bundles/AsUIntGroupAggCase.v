module AsUIntGroupAggCase (
  input  [9:0]  in_a,
  input         in_bar_c_0,
  input  [19:0] in_bar_d_0,
  input         in_bar_c_1,
  input  [19:0] in_bar_d_1,
  input  [10:0] in_b,
  output [62:0] out
);
  wire [10:0] in_5_4;
  wire [20:0] in_3_2;
  wire [30:0] in_1_0;
  wire [31:0] in_5_2;
  assign out = {in_5_2, in_1_0};
  assign in_5_4 = {in_a, in_bar_c_1};
  assign in_3_2 = {in_bar_d_1, in_bar_c_0};
  assign in_1_0 = {in_bar_d_0, in_b};
  assign in_5_2 = {in_5_4, in_3_2};
endmodule
