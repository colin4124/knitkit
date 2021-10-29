module Inner (
  input        valid,
  output [3:0] rdata,
  output       ready
);
  assign rdata = 4'h5;
  assign ready = 1'h1;
endmodule
module Middle (
  output [3:0] addr,
  output [3:0] wdata,
  input        tx_ABC_todo
);
  Inner u_inner (
    .valid ( tx_ABC_todo ),
    .rdata ( wdata       ),
    .ready ( addr        )
  );
endmodule
module TopCloneCase (
  input        BAR,
  output [3:0] FOO,
  output [3:0] CAR
);
  Middle u_middle (
    .addr        ( FOO ),
    .wdata       ( CAR ),
    .tx_ABC_todo ( BAR )
  );
endmodule
