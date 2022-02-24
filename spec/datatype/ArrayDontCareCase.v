module ArrayDontCareCase (
  input  [6:0] x_in[0:3][0:7],
  output [6:0] x_out[0:3][0:7],
  input  [5:0] y_in[0:1][0:3],
  output [5:0] y_out[0:3][0:3]
);
  assign x_out[0][0] = 1'h0;
  assign x_out[1][0] = 1'h0;
  assign x_out[2][0] = 1'h0;
  assign x_out[3][0] = 1'h0;
  assign x_out[0][1] = 1'h0;
  assign x_out[1][1] = 1'h0;
  assign x_out[2][1] = 1'h0;
  assign x_out[3][1] = 1'h0;
  assign x_out[0][2] = 1'h0;
  assign x_out[1][2] = 1'h0;
  assign x_out[2][2] = 1'h0;
  assign x_out[3][2] = 1'h0;
  assign x_out[0][3] = 1'h0;
  assign x_out[1][3] = 1'h0;
  assign x_out[2][3] = 1'h0;
  assign x_out[3][3] = 1'h0;
  assign x_out[0][4] = 1'h0;
  assign x_out[1][4] = 1'h0;
  assign x_out[2][4] = 1'h0;
  assign x_out[3][4] = 1'h0;
  assign x_out[0][5] = 1'h0;
  assign x_out[1][5] = 1'h0;
  assign x_out[2][5] = 1'h0;
  assign x_out[3][5] = 1'h0;
  assign x_out[0][6] = 1'h0;
  assign x_out[1][6] = 1'h0;
  assign x_out[2][6] = 1'h0;
  assign x_out[3][6] = 1'h0;
  assign x_out[0][7] = 1'h0;
  assign x_out[1][7] = 1'h0;
  assign x_out[2][7] = 1'h0;
  assign x_out[3][7] = 1'h0;
  assign y_out[0][0] = 1'h0;
  assign y_out[1][0] = 1'h0;
  assign y_out[2][0] = 1'h0;
  assign y_out[3][0] = 1'h0;
  assign y_out[0][1] = 1'h0;
  assign y_out[1][1] = 1'h0;
  assign y_out[2][1] = 1'h0;
  assign y_out[3][1] = 1'h0;
  assign y_out[0][2] = 1'h0;
  assign y_out[1][2] = 1'h0;
  assign y_out[2][2] = 1'h0;
  assign y_out[3][2] = 1'h0;
  assign y_out[0][3] = 1'h0;
  assign y_out[1][3] = 1'h0;
  assign y_out[2][3] = 1'h0;
  assign y_out[3][3] = 1'h0;
endmodule
