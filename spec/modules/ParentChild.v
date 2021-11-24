module Add (
  input  [31:0] in0,
  input  [31:0] in1,
  output [31:0] out
);
  assign out = in0 + in1;
endmodule
module ParentChild (
  input  [3:0]  in0,
  input  [3:0]  in1,
  output [31:0] out
);
  wire [31:0] u_add_out;
  assign out = u_add_out + 4'h9;
  Add u_add (
    .in0 ( {in0, 4'ha}  ),
    .in1 ( {3'h5, 1'h1} ),
    .out ( u_add_out    )
  );
endmodule
