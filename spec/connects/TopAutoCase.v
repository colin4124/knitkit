module A (
  input        valid,
  output [3:0] rdata,
  output       ready
);
  assign rdata = 4'h5;
  assign ready = 1'h1;
endmodule
module B (
  output [3:0] addr,
  output [3:0] wdata,
  input        valid,
  output [3:0] rdata,
  output       ready
);
  assign addr = 4'ha;
  assign wdata = 4'h4;
  A u_a (
    .valid ( valid ),
    .rdata ( rdata ),
    .ready ( ready )
  );
endmodule
module TopAutoCase (
  output [3:0] out,
  output [3:0] wdata,
  input        valid,
  output [3:0] rdata,
  output       ready
);
  wire [3:0] u_b_addr;
  assign out = u_b_addr + 3'h4;
  B u_b (
    .addr  ( u_b_addr ),
    .wdata ( wdata    ),
    .valid ( valid    ),
    .rdata ( rdata    ),
    .ready ( ready    )
  );
endmodule
