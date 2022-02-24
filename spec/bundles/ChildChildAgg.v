module SlaveAgg (
  input        slv_valid,
  input  [3:0] slv_addr,
  input  [3:0] slv_wdata,
  output [3:0] slv_rdata,
  output       slv_ready,
  output [4:0] bus_out
);
  assign slv_rdata = slv_wdata;
  assign slv_ready = slv_valid;
  assign bus_out = slv_valid | slv_addr | slv_wdata;
endmodule
module MasterAgg (
  output       mst_valid,
  output [3:0] mst_addr,
  output [3:0] mst_wdata,
  input  [3:0] mst_rdata,
  input        mst_ready,
  output [4:0] bus_out
);
  assign mst_valid = 1'h1;
  assign mst_addr = 4'hc;
  assign mst_wdata = 4'hc;
  assign bus_out = mst_ready & mst_rdata;
endmodule
module ChildChildAgg (
  output [4:0] out
);
  wire [3:0]  u_slave_slv_rdata_to_u_master_mst_rdata;
  wire        u_slave_slv_ready_to_u_master_mst_ready;
  wire [4:0]  u_slave_bus_out;
  wire        u_master_mst_valid_to_u_slave_slv_valid;
  wire [3:0]  u_master_mst_addr_to_u_slave_slv_addr;
  wire [3:0]  u_master_mst_wdata_to_u_slave_slv_wdata;
  wire [4:0]  u_master_bus_out;

  assign out = u_slave_bus_out | u_master_bus_out;

  SlaveAgg u_slave (
    .slv_valid ( u_master_mst_valid_to_u_slave_slv_valid ),
    .slv_addr  ( u_master_mst_addr_to_u_slave_slv_addr   ),
    .slv_wdata ( u_master_mst_wdata_to_u_slave_slv_wdata ),
    .slv_rdata ( u_slave_slv_rdata_to_u_master_mst_rdata ),
    .slv_ready ( u_slave_slv_ready_to_u_master_mst_ready ),
    .bus_out   ( u_slave_bus_out                         )
  );

  MasterAgg u_master (
    .mst_valid ( u_master_mst_valid_to_u_slave_slv_valid ),
    .mst_addr  ( u_master_mst_addr_to_u_slave_slv_addr   ),
    .mst_wdata ( u_master_mst_wdata_to_u_slave_slv_wdata ),
    .mst_rdata ( u_slave_slv_rdata_to_u_master_mst_rdata ),
    .mst_ready ( u_slave_slv_ready_to_u_master_mst_ready ),
    .bus_out   ( u_master_bus_out                        )
  );
endmodule
