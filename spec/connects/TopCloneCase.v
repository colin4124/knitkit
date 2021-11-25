module Inner (
  input        valid,
  output [3:0] rdata,
  output       ready
);
  assign rdata = 4'h5;
  assign ready = valid;
endmodule
module Middle (
  output       ready,
  output [3:0] rdata,
  input        tx_ABC_todo
);
  Inner u_inner (
    .valid ( tx_ABC_todo ),
    .rdata ( rdata       ),
    .ready ( ready       )
  );
endmodule
module TopCloneCase (
  input        BAR,
  output       FOO,
  output [3:0] CAR
);
  Middle u_middle (
    .ready       ( FOO ),
    .rdata       ( CAR ),
    .tx_ABC_todo ( BAR )
  );
endmodule
