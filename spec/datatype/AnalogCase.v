module AnalogCase (
  input  I,
  input  OEN,
  output O,
  inout  Pad
);
  IOPad pad (
    .I   ( I   ),
    .OEN ( OEN ),
    .O   ( O   ),
    .Pad ( Pad )
  );
endmodule
