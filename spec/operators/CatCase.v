module CatCase (
  output [73:0] out
);
  wire [9:0]   out_19_18;
  wire [9:0]   out_17_16;
  wire [8:0]   out_15_14;
  wire [7:0]   out_13_12;
  wire [7:0]   out_11_10;
  wire [7:0]   out_9_8;
  wire [6:0]   out_7_6;
  wire [5:0]   out_5_4;
  wire [4:0]   out_3_2;
  wire [2:0]   out_1_0;
  wire [19:0]  out_19_16;
  wire [16:0]  out_15_12;
  wire [15:0]  out_11_8;
  wire [12:0]  out_7_4;
  wire [7:0]   out_3_0;
  wire [36:0]  out_19_12;
  wire [28:0]  out_11_4;
  wire [65:0]  out_19_4;

  assign out = {out_19_4, out_3_0};
  assign out_19_18 = {5'h14, 5'h13};
  assign out_17_16 = {5'h12, 5'h11};
  assign out_15_14 = {5'h10, 4'hf};
  assign out_13_12 = {4'he, 4'hd};
  assign out_11_10 = {4'hc, 4'hb};
  assign out_9_8 = {4'ha, 4'h9};
  assign out_7_6 = {4'h8, 3'h7};
  assign out_5_4 = {3'h6, 3'h5};
  assign out_3_2 = {3'h4, 2'h3};
  assign out_1_0 = {2'h2, 1'h1};
  assign out_19_16 = {out_19_18, out_17_16};
  assign out_15_12 = {out_15_14, out_13_12};
  assign out_11_8 = {out_11_10, out_9_8};
  assign out_7_4 = {out_7_6, out_5_4};
  assign out_3_0 = {out_3_2, out_1_0};
  assign out_19_12 = {out_19_16, out_15_12};
  assign out_11_4 = {out_11_8, out_7_4};
  assign out_19_4 = {out_19_12, out_11_4};
endmodule
