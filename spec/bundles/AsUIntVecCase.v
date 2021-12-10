module AsUIntVecCase (
  input  [9:0]   in_a_0,
  input          in_bar_c_0_0,
  input  [19:0]  in_bar_d_0_0,
  input          in_bar_c_0_1,
  input  [19:0]  in_bar_d_0_1,
  input  [10:0]  in_b_0,
  input  [9:0]   in_a_1,
  input          in_bar_c_1_0,
  input  [19:0]  in_bar_d_1_0,
  input          in_bar_c_1_1,
  input  [19:0]  in_bar_d_1_1,
  input  [10:0]  in_b_1,
  output [125:0] out
);
  assign out = {in_a_1, in_bar_c_1_1, in_bar_d_1_1, in_bar_c_1_0, in_bar_d_1_0, in_b_1, in_a_0, in_bar_c_0_1, in_bar_d_0_1, in_bar_c_0_0, in_bar_d_0_0, in_b_0};
endmodule
