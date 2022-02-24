module AddBeta (
  input  [31:0] in0,
  input  [31:0] in1,
  output [31:0] out,
  output [31:0] out1
);
  assign out = in0 + in1;
  assign out1 = in0 + in1;
endmodule
module ParentChildBeta (
  input  [27:0] in0,
  input  [3:0]  in1,
  output [31:0] out
);
  wire [31:0]  foo;
  wire [31:0]  u_add_out;

  assign out = u_add_out & foo;

  AddBeta u_add (
    .in0  ( {in0, 4'ha}   ),
    .in1  ( {31'h5, 1'h1} ),
    .out  ( u_add_out     ),
    .out1 ( foo           )
  );
endmodule
