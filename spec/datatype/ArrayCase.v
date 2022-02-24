module ArrayCase (
  input        clk,
  input        rstn,
  input        sel_1,
  input        sel_2,
  input  [7:0] y_mem[0:3]
);
  wire [7:0]  y2[0:3];
  reg  [5:0]  R_old[0:3][0:5];
  reg         y1[0:2][0:3][0:5];
  reg  [5:0]  y3[0:1][0:3];
  reg  [5:0]  y4[0:1][0:3];

  assign y2[0] = y_mem[0];
  assign y2[1] = y_mem[1];
  assign y2[2] = y_mem[2];
  assign y2[3] = y_mem[3];

  ArraySub sub (
    .y_mem ( y_mem )
  );

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][0] <= 1'h0;
    end
    else begin
      y1[0][0][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][0] <= 1'h0;
    end
    else begin
      y1[1][0][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][0] <= 1'h0;
    end
    else begin
      y1[2][0][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][0] <= 1'h0;
    end
    else begin
      y1[0][1][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][0] <= 1'h0;
    end
    else begin
      y1[1][1][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][0] <= 1'h0;
    end
    else begin
      y1[2][1][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][0] <= 1'h0;
    end
    else begin
      y1[0][2][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][0] <= 1'h0;
    end
    else begin
      y1[1][2][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][0] <= 1'h0;
    end
    else begin
      y1[2][2][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][0] <= 1'h0;
    end
    else begin
      y1[0][3][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][0] <= 1'h0;
    end
    else begin
      y1[1][3][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][0] <= 1'h0;
    end
    else begin
      y1[2][3][0] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][1] <= 1'h0;
    end
    else begin
      y1[0][0][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][1] <= 1'h0;
    end
    else begin
      y1[1][0][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][1] <= 1'h0;
    end
    else begin
      y1[2][0][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][1] <= 1'h0;
    end
    else begin
      y1[0][1][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][1] <= 1'h0;
    end
    else begin
      y1[1][1][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][1] <= 1'h0;
    end
    else begin
      y1[2][1][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][1] <= 1'h0;
    end
    else begin
      y1[0][2][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][1] <= 1'h0;
    end
    else begin
      y1[1][2][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][1] <= 1'h0;
    end
    else begin
      y1[2][2][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][1] <= 1'h0;
    end
    else begin
      y1[0][3][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][1] <= 1'h0;
    end
    else begin
      y1[1][3][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][1] <= 1'h0;
    end
    else begin
      y1[2][3][1] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][2] <= 1'h0;
    end
    else begin
      y1[0][0][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][2] <= 1'h0;
    end
    else begin
      y1[1][0][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][2] <= 1'h0;
    end
    else begin
      y1[2][0][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][2] <= 1'h0;
    end
    else begin
      y1[0][1][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][2] <= 1'h0;
    end
    else begin
      y1[1][1][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][2] <= 1'h0;
    end
    else begin
      y1[2][1][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][2] <= 1'h0;
    end
    else begin
      y1[0][2][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][2] <= 1'h0;
    end
    else begin
      y1[1][2][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][2] <= 1'h0;
    end
    else begin
      y1[2][2][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][2] <= 1'h0;
    end
    else begin
      y1[0][3][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][2] <= 1'h0;
    end
    else begin
      y1[1][3][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][2] <= 1'h0;
    end
    else begin
      y1[2][3][2] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][3] <= 1'h0;
    end
    else begin
      y1[0][0][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][3] <= 1'h0;
    end
    else begin
      y1[1][0][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][3] <= 1'h0;
    end
    else begin
      y1[2][0][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][3] <= 1'h0;
    end
    else begin
      y1[0][1][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][3] <= 1'h0;
    end
    else begin
      y1[1][1][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][3] <= 1'h0;
    end
    else begin
      y1[2][1][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][3] <= 1'h0;
    end
    else begin
      y1[0][2][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][3] <= 1'h0;
    end
    else begin
      y1[1][2][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][3] <= 1'h0;
    end
    else begin
      y1[2][2][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][3] <= 1'h0;
    end
    else begin
      y1[0][3][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][3] <= 1'h0;
    end
    else begin
      y1[1][3][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][3] <= 1'h0;
    end
    else begin
      y1[2][3][3] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][4] <= 1'h0;
    end
    else begin
      y1[0][0][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][4] <= 1'h0;
    end
    else begin
      y1[1][0][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][4] <= 1'h0;
    end
    else begin
      y1[2][0][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][4] <= 1'h0;
    end
    else begin
      y1[0][1][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][4] <= 1'h0;
    end
    else begin
      y1[1][1][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][4] <= 1'h0;
    end
    else begin
      y1[2][1][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][4] <= 1'h0;
    end
    else begin
      y1[0][2][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][4] <= 1'h0;
    end
    else begin
      y1[1][2][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][4] <= 1'h0;
    end
    else begin
      y1[2][2][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][4] <= 1'h0;
    end
    else begin
      y1[0][3][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][4] <= 1'h0;
    end
    else begin
      y1[1][3][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][4] <= 1'h0;
    end
    else begin
      y1[2][3][4] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][0][5] <= 1'h0;
    end
    else begin
      y1[0][0][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][0][5] <= 1'h0;
    end
    else begin
      y1[1][0][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][0][5] <= 1'h0;
    end
    else begin
      y1[2][0][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][1][5] <= 1'h0;
    end
    else begin
      y1[0][1][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][1][5] <= 1'h0;
    end
    else begin
      y1[1][1][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][1][5] <= 1'h0;
    end
    else begin
      y1[2][1][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][2][5] <= 1'h0;
    end
    else begin
      y1[0][2][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][2][5] <= 1'h0;
    end
    else begin
      y1[1][2][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][2][5] <= 1'h0;
    end
    else begin
      y1[2][2][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[0][3][5] <= 1'h0;
    end
    else begin
      y1[0][3][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[1][3][5] <= 1'h0;
    end
    else begin
      y1[1][3][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y1[2][3][5] <= 1'h0;
    end
    else begin
      y1[2][3][5] <= 1'h1;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y4[1][3] <= 6'h0;
    end
    else begin
      y4[1][3] <= 6'h18;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y3[1][0] <= 6'h0;
    end
    else begin
      y3[1][0] <= 6'h3b;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y3[1][1] <= 6'h0;
    end
    else begin
      y3[1][1] <= 6'h3b;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y3[1][2] <= 6'h0;
    end
    else begin
      y3[1][2] <= 6'h3b;
    end
  end

  always @(posedge clk or negedge rstn) begin
    if (!rstn) begin
      y3[1][3] <= 6'h0;
    end
    else begin
      y3[1][3] <= 6'h3b;
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][0] <= y1[1][0][0];
    end
    else if (sel_2) begin
      R_old[0][0] <= y1[2][0][0];
    end
    else begin
      R_old[0][0] <= y1[0][0][0];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][0] <= y1[1][1][0];
    end
    else if (sel_2) begin
      R_old[1][0] <= y1[2][1][0];
    end
    else begin
      R_old[1][0] <= y1[0][1][0];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][0] <= y1[1][2][0];
    end
    else if (sel_2) begin
      R_old[2][0] <= y1[2][2][0];
    end
    else begin
      R_old[2][0] <= y1[0][2][0];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][0] <= y1[1][3][0];
    end
    else if (sel_2) begin
      R_old[3][0] <= y1[2][3][0];
    end
    else begin
      R_old[3][0] <= y1[0][3][0];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][1] <= y1[1][0][1];
    end
    else if (sel_2) begin
      R_old[0][1] <= y1[2][0][1];
    end
    else begin
      R_old[0][1] <= y1[0][0][1];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][1] <= y1[1][1][1];
    end
    else if (sel_2) begin
      R_old[1][1] <= y1[2][1][1];
    end
    else begin
      R_old[1][1] <= y1[0][1][1];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][1] <= y1[1][2][1];
    end
    else if (sel_2) begin
      R_old[2][1] <= y1[2][2][1];
    end
    else begin
      R_old[2][1] <= y1[0][2][1];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][1] <= y1[1][3][1];
    end
    else if (sel_2) begin
      R_old[3][1] <= y1[2][3][1];
    end
    else begin
      R_old[3][1] <= y1[0][3][1];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][2] <= y1[1][0][2];
    end
    else if (sel_2) begin
      R_old[0][2] <= y1[2][0][2];
    end
    else begin
      R_old[0][2] <= y1[0][0][2];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][2] <= y1[1][1][2];
    end
    else if (sel_2) begin
      R_old[1][2] <= y1[2][1][2];
    end
    else begin
      R_old[1][2] <= y1[0][1][2];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][2] <= y1[1][2][2];
    end
    else if (sel_2) begin
      R_old[2][2] <= y1[2][2][2];
    end
    else begin
      R_old[2][2] <= y1[0][2][2];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][2] <= y1[1][3][2];
    end
    else if (sel_2) begin
      R_old[3][2] <= y1[2][3][2];
    end
    else begin
      R_old[3][2] <= y1[0][3][2];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][3] <= y1[1][0][3];
    end
    else if (sel_2) begin
      R_old[0][3] <= y1[2][0][3];
    end
    else begin
      R_old[0][3] <= y1[0][0][3];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][3] <= y1[1][1][3];
    end
    else if (sel_2) begin
      R_old[1][3] <= y1[2][1][3];
    end
    else begin
      R_old[1][3] <= y1[0][1][3];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][3] <= y1[1][2][3];
    end
    else if (sel_2) begin
      R_old[2][3] <= y1[2][2][3];
    end
    else begin
      R_old[2][3] <= y1[0][2][3];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][3] <= y1[1][3][3];
    end
    else if (sel_2) begin
      R_old[3][3] <= y1[2][3][3];
    end
    else begin
      R_old[3][3] <= y1[0][3][3];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][4] <= y1[1][0][4];
    end
    else if (sel_2) begin
      R_old[0][4] <= y1[2][0][4];
    end
    else begin
      R_old[0][4] <= y1[0][0][4];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][4] <= y1[1][1][4];
    end
    else if (sel_2) begin
      R_old[1][4] <= y1[2][1][4];
    end
    else begin
      R_old[1][4] <= y1[0][1][4];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][4] <= y1[1][2][4];
    end
    else if (sel_2) begin
      R_old[2][4] <= y1[2][2][4];
    end
    else begin
      R_old[2][4] <= y1[0][2][4];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][4] <= y1[1][3][4];
    end
    else if (sel_2) begin
      R_old[3][4] <= y1[2][3][4];
    end
    else begin
      R_old[3][4] <= y1[0][3][4];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[0][5] <= y1[1][0][5];
    end
    else if (sel_2) begin
      R_old[0][5] <= y1[2][0][5];
    end
    else begin
      R_old[0][5] <= y1[0][0][5];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[1][5] <= y1[1][1][5];
    end
    else if (sel_2) begin
      R_old[1][5] <= y1[2][1][5];
    end
    else begin
      R_old[1][5] <= y1[0][1][5];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[2][5] <= y1[1][2][5];
    end
    else if (sel_2) begin
      R_old[2][5] <= y1[2][2][5];
    end
    else begin
      R_old[2][5] <= y1[0][2][5];
    end
  end

  always @* begin
    if (sel_1) begin
      R_old[3][5] <= y1[1][3][5];
    end
    else if (sel_2) begin
      R_old[3][5] <= y1[2][3][5];
    end
    else begin
      R_old[3][5] <= y1[0][3][5];
    end
  end
endmodule
