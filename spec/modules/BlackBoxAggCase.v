module BlackBoxAggCase (
  input  clk_125M,
  input  clk_25M,
  input  sel,
  output clk_out
);
  wire  u_ibufds_O;
  wire  u_ibuf_O;

  assign clk_out = sel ? u_ibufds_O : u_ibuf_O;

  IBUFDS #(
    .DIFF_TERM  ("TRUE"   ),
    .IOSTANDARD ("DEFAULT")
  ) u_ibufds (
    .O  ( u_ibufds_O ),
    .I  ( clk_125M   ),
    .IB ( clk_25M    )
  );

  IBUFAgg u_ibuf (
    .O  ( u_ibuf_O ),
    .I  ( clk_125M ),
    .IB ( clk_25M  )
  );
endmodule
