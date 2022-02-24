module WireCase (
  output [7:0] out1,
  output [7:0] out2
);
  wire [7:0]  my_node;
  wire [7:0]  init_node;

  assign out1 = my_node;
  assign out2 = init_node;
  assign my_node = 8'ha;
  assign init_node = 8'ha;
endmodule
