module Piece_8 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 4'h8;
endmodule
module Piece_2 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 2'h2;
endmodule
module Piece_3 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 2'h3;
endmodule
module Piece_9 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 4'h9;
endmodule
module Piece_4 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 3'h4;
endmodule
module Piece_5 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 3'h5;
endmodule
module Piece (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 1'h0;
endmodule
module AbundantModules (
  input  [3:0] in0_0,
  input  [3:0] in0_1,
  input  [3:0] in0_2,
  input  [3:0] in0_3,
  input  [3:0] in0_4,
  input  [3:0] in0_5,
  input  [3:0] in0_6,
  input  [3:0] in0_7,
  input  [3:0] in0_8,
  input  [3:0] in0_9,
  output [3:0] out
);
  wire [31:0]  out_res_9_2;
  wire [7:0]   out_res_1_0;
  wire [39:0]  out_res;
  wire [3:0]   vec_m_0_out;
  wire [3:0]   vec_m_1_out;
  wire [3:0]   vec_m_2_out;
  wire [3:0]   vec_m_3_out;
  wire [3:0]   vec_m_4_out;
  wire [3:0]   vec_m_5_out;
  wire [3:0]   vec_m_6_out;
  wire [3:0]   vec_m_7_out;
  wire [3:0]   vec_m_8_out;
  wire [3:0]   vec_m_9_out;

  assign out = ^out_res;
  assign out_res_9_2 = {vec_m_0_out, vec_m_1_out, vec_m_2_out, vec_m_3_out, vec_m_4_out, vec_m_5_out, vec_m_6_out, vec_m_7_out};
  assign out_res_1_0 = {vec_m_8_out, vec_m_9_out};
  assign out_res = {out_res_9_2, out_res_1_0};

  Piece vec_m_0 (
    .in0 ( in0_0       ),
    .out ( vec_m_0_out )
  );

  Piece_1 vec_m_1 (
    .in0 ( in0_1       ),
    .out ( vec_m_1_out )
  );

  Piece_2 vec_m_2 (
    .in0 ( in0_2       ),
    .out ( vec_m_2_out )
  );

  Piece_3 vec_m_3 (
    .in0 ( in0_3       ),
    .out ( vec_m_3_out )
  );

  Piece_4 vec_m_4 (
    .in0 ( in0_4       ),
    .out ( vec_m_4_out )
  );

  Piece_5 vec_m_5 (
    .in0 ( in0_5       ),
    .out ( vec_m_5_out )
  );

  Piece_6 vec_m_6 (
    .in0 ( in0_6       ),
    .out ( vec_m_6_out )
  );

  Piece_7 vec_m_7 (
    .in0 ( in0_7       ),
    .out ( vec_m_7_out )
  );

  Piece_8 vec_m_8 (
    .in0 ( in0_8       ),
    .out ( vec_m_8_out )
  );

  Piece_9 vec_m_9 (
    .in0 ( in0_9       ),
    .out ( vec_m_9_out )
  );
endmodule
module Piece_6 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 3'h6;
endmodule
module Piece_1 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 1'h1;
endmodule
module Piece_7 (
  input  [3:0] in0,
  output [3:0] out
);
  assign out = in0 & 3'h7;
endmodule
