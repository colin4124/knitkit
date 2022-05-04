module AsUIntBundleCase (
  input  [9:0]  in_a,
  input         in_bar_c_0,
  input  [19:0] in_bar_d_0,
  input         in_bar_c_1,
  input  [19:0] in_bar_d_1,
  input  [10:0] in_b,
  output [62:0] out
);
  assign out = {in_a, in_bar_c_1, in_bar_d_1, in_bar_c_0, in_bar_d_0, in_b};
endmodule
