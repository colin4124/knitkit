module Slave (
  input        valid,
  input  [3:0] addr,
  input  [3:0] wdata,
  output [3:0] rdata,
  output       ready,
  output [4:0] bus_out
);
  assign rdata = wdata;
  assign ready = valid;
  assign bus_out = valid | addr | wdata | rdata;
endmodule
module Master (
  output       valid,
  output [3:0] addr,
  output [3:0] wdata,
  input  [3:0] rdata,
  input        ready,
  output [4:0] bus_out
);
  assign valid = 1'h1;
  assign addr = 4'hc;
  assign wdata = 4'hc;
  assign bus_out = ready & rdata;
endmodule
module ChildChildBeta (
  output [4:0] out
);
  wire [3:0]  u_slave_rdata_to_u_master_rdata;
  wire        u_slave_ready_to_u_master_ready;
  wire [4:0]  u_slave_bus_out;
  wire        u_master_valid_to_u_slave_valid;
  wire [3:0]  u_master_addr_to_u_slave_addr;
  wire [3:0]  u_master_wdata_to_u_slave_wdata;
  wire [4:0]  u_master_bus_out;

  assign out = u_slave_bus_out | u_master_bus_out | u_master_valid_to_u_slave_valid;

  Slave u_slave (
    .valid   ( u_master_valid_to_u_slave_valid ),
    .addr    ( u_master_addr_to_u_slave_addr   ),
    .wdata   ( u_master_wdata_to_u_slave_wdata ),
    .rdata   ( u_slave_rdata_to_u_master_rdata ),
    .ready   ( u_slave_ready_to_u_master_ready ),
    .bus_out ( u_slave_bus_out                 )
  );

  Master u_master (
    .valid   ( u_master_valid_to_u_slave_valid ),
    .addr    ( u_master_addr_to_u_slave_addr   ),
    .wdata   ( u_master_wdata_to_u_slave_wdata ),
    .rdata   ( u_slave_rdata_to_u_master_rdata ),
    .ready   ( u_slave_ready_to_u_master_ready ),
    .bus_out ( u_master_bus_out                )
  );
endmodule
