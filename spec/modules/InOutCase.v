module InOutCase (
  input  I,
  input  OEN,
  output O,
  inout  Pad
);
  IOPadInOut pad (
    .I   ( I   ),
    .OEN ( OEN ),
    .O   ( O   ),
    .Pad ( Pad )
  );
endmodule
