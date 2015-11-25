package com.mdp.grp2.batmobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class MapGridView extends View {
    public float map_width;
    public float map_Height;
    public int map_NoOfRows;
    public int map_NoOfCols;
    public float map_XOffset;
    public float map_YOffset;
    public String[] mapGridArr;
    public int[][] obstacleArr; // order Row and then Column respectively
    public int nonObstacleOffset = 7;
    public int drawOption = 0;
    public int x = 0;
    public int y = 0;
    public int x_rbh = 0;
    public int y_rbh = 0;
    public int x_rbb = 0;
    public int y_rbb = 0;
//    public int[] robotPartX = new int[]{10};
//    public int[] robotPartY = new int[]{9};
    public int robotRearX;
    public int robotRearY;
    public int robotHeadX;
    public int robotHeadY;

    public static final float DEFAULT_X_OFFSET = 20;
    public static final float DEFAULT_Y_OFFSET = 10;
    public static final int DEFAULT_NO_ROWS = 15;
    public static final int DEFAULT_NO_COLS = 20;
    public static final float DEFAULT_WIDTH = 31;
    public static final float DEFAULT_HEIGHT = 31;

    public static final int INIT_GRID = 0;
    public static final int RESET_GRID = 1;
    public static final int CREATE_OBSTACLE = 2;
    public static final int CREATE_RBHEAD = 3;
    public static final int CREATE_RBBODY = 4;
    public static final int CREATE_RBPART = 5;


    Paint paint = new Paint();
    Paint paintRec = new Paint();
    MainActivity rb = new MainActivity();

    public int[] robotPartX = new int[]{0,0,0,0,0,0,0,0,0};
    public int[] robotPartY = new int[]{0,0,0,0,0,0,0,0,0};

    public void robotPos(){
        // 0 1 2
        // 3 4 5
        // 6 7 8
        robotPartX[0] = robotRearX - 1;
        robotPartY[0] = robotRearY + 1;

        robotPartX[1] = robotRearX;
        robotPartY[1] = robotRearY + 1;

        robotPartX[2] = robotRearX + 1;
        robotPartY[2] = robotRearY + 1;

        robotPartX[3] = robotRearX - 1;
        robotPartY[3] = robotRearY;

        robotPartX[4] = robotRearX;
        robotPartY[4] = robotRearY;

        robotPartX[5] = robotRearX + 1;
        robotPartY[5] = robotRearY;

        robotPartX[6] = robotRearX - 1;
        robotPartY[6] = robotRearY -1;

        robotPartX[7] = robotRearX;
        robotPartY[7] = robotRearY -1;

        robotPartX[8] = robotRearX + 1;
        robotPartY[8] = robotRearY -1;
    }


    public MapGridView(Context context) {
        super(context);
        InitValues();

        this.drawOption = INIT_GRID;

    }

    // Overload Constructor
    public MapGridView(Context context, String[] gridArr) {
        super(context);
        InitValues();
        this.map_NoOfCols = Integer.parseInt(gridArr[2]);
        this.map_NoOfRows = Integer.parseInt(gridArr[1]);
        this.robotRearX = Integer.parseInt(gridArr[3]) -1;
        this.robotRearY = Integer.parseInt(gridArr[4]) -1;
        this.robotHeadX = Integer.parseInt(gridArr[5]) -1;
        this.robotHeadY = Integer.parseInt(gridArr[6]) -1;


        // Gets two dimensional array with obstacles and init for plotting on map
        obstacleArr = getObstacleArr(gridArr, this.map_NoOfRows, this.map_NoOfCols);


        this.drawOption = RESET_GRID;
    }


    // Overload Constructor
    // For drawing obstacle and robot head
    public MapGridView(Context context, int x, int y, int option) {
        super(context);
        InitValues();
        if (option == CREATE_OBSTACLE) {
            this.drawOption = CREATE_OBSTACLE;
            this.x = x;
            this.y = y;
        } else if (option == CREATE_RBHEAD) {
            this.drawOption = CREATE_RBHEAD;
            this.x_rbh = x;
            this.y_rbh = y;
        } else if (option == CREATE_RBBODY) {
            this.drawOption = CREATE_RBBODY;
            this.x_rbb = x;
            this.y_rbb = y;
        }

    }

    public MapGridView(Context context, int[] x, int[] y, int option) {
        super(context);
        InitValues();

        if (option == CREATE_RBPART) {
            this.drawOption = CREATE_RBPART;
            this.robotPartX = x;
            this.robotPartY = y;
        }


    }


    @Override
    public void onDraw(Canvas canvas) {
        float X = DEFAULT_X_OFFSET;
        float Y = DEFAULT_Y_OFFSET;
        float iRow = DEFAULT_NO_ROWS;
        float iCol = DEFAULT_NO_COLS;

        // This option will initalize grid on the map on start up
        if (this.drawOption == INIT_GRID) {
            initGrid(canvas, X, Y, iRow, iCol);

        }
        // This option will reset and redraw the entire grid with Robot and Obstacles
        else if (this.drawOption == RESET_GRID) {

            resetGrid(canvas, X, Y, iRow, iCol);

        } // This option will draw OBSTACLES
        else if (this.drawOption == CREATE_OBSTACLE) {

            createObstacle(this.x, this.y, canvas);

        } else if (this.drawOption == CREATE_RBHEAD) {

            createRobotHead(this.x_rbh, this.y_rbh, canvas);

        } else if (this.drawOption == CREATE_RBBODY) {

            createRobotBody(this.x_rbb, this.y_rbb, canvas);

        } else if (this.drawOption == CREATE_RBPART) {

            createRobotPart(this.robotPartX, this.robotPartY, canvas);

        }
    }

    //init grid
    private void initGrid (Canvas canvas, float X, float Y, float iRow, float iCol){
        // Draw the rows
        for (iRow = 0; iRow < map_NoOfRows; iRow++) {
            // Draw The coloumns
            for (iCol = 0; iCol < map_NoOfCols; iCol++) {
                paintRec.setColor(Color.BLUE);
                paintRec.setStyle(Paint.Style.STROKE);
                paintRec.setStrokeWidth(3);


                canvas.drawRect(X + (iCol * map_width), Y + (iRow * map_Height), X
                        + (iCol * map_width) + map_width, Y + (iRow * map_Height)
                        + map_Height, paintRec);
            }

        }
    }

    //resets the grid
    public void resetGrid(Canvas canvas, float X, float Y, float iRow,float iCol) {
        robotPos();
        createRobotPart(robotPartX,robotPartY,canvas);
        for (iRow = 0; iRow < map_NoOfRows; iRow++) {
            // Draw The coloumns
            for (iCol = 0; iCol < map_NoOfCols; iCol++) {
                paintRec.setColor(Color.BLUE);
                paintRec.setStyle(Paint.Style.STROKE);
                paintRec.setStrokeWidth(3);

                canvas.drawRect(X + (iCol * map_width), Y + (iRow * map_Height), X
                        + (iCol * map_width) + map_width, Y + (iRow * map_Height)
                        + map_Height, paintRec);

                // Creates obstacle onto the grid map
                if (obstacleArr != null)
                    createObstacles(iCol, iRow, paint, X, Y, canvas, obstacleArr);


            }

        }
    }

    // Creates obstacles on the grid map
    // obstacleArr contains position of all obstacles on the map
    private void createObstacles(float iCol, float iRow, Paint paint, float X, float Y, Canvas canvas, int[][] obstacleArr) {

        if (obstacleArr[(int) iRow][(int) iCol] == 1) {
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(X + (iCol * map_width),
                    Y + (iRow * map_Height), X + (iCol * map_width)
                            + map_width,
                    Y + (iRow * map_Height) + map_Height, paint);
        }
        //robot head pos??
        else if (obstacleArr[(int) iRow][(int) iCol] == 2) {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(X + (iCol * map_width),
                    Y + (iRow * map_Height), X + (iCol * map_width)
                            + map_width,
                    Y + (iRow * map_Height) + map_Height, paint);
        }
        //robot body pos??
        else if (obstacleArr[(int) iRow][(int) iCol] == 3) {
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(X + (iCol * map_width),
                    Y + (iRow * map_Height), X + (iCol * map_width)
                            + map_width,
                    Y + (iRow * map_Height) + map_Height, paint);
        }

        if (iRow == robotRearY){
            if (iCol == robotRearX){
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(X + (iCol * map_width),
                        Y + (iRow * map_Height), X + (iCol * map_width)
                                + map_width,
                        Y + (iRow * map_Height) + map_Height, paint);
            }
        }

        if (iRow == robotHeadY){
            if (iCol == robotHeadX){
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(X + (iCol * map_width),
                        Y + (iRow * map_Height), X + (iCol * map_width)
                                + map_width,
                        Y + (iRow * map_Height) + map_Height, paint);
            }
        }



    }


    // Creates obstacles on the grid map
    // obstacleArr contains position of all obstacles on the map
    public void createObstacle(float iCol, float iRow, Canvas canvas) {

        float X = DEFAULT_X_OFFSET;
        float Y = DEFAULT_Y_OFFSET;

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(X + (iCol * map_width),
                Y + (iRow * map_Height), X + (iCol * map_width)
                        + map_width,
                Y + (iRow * map_Height) + map_Height, paint);


    }


    // Function populates a 2 dimensional array with obstacles
    // Returns the 2 dimensional array
    private int[][] getObstacleArr(String[] gridArr, int m_NoOfRows, int m_NoOfCols) {

        // Initalize position pointer is at for y position
        int row = 0;
        int col = 0;
        int maxCells = m_NoOfCols * m_NoOfRows;
        obstacleArr = new int[m_NoOfRows][m_NoOfCols];

        // Creates a two dimensional array that shows obstacles
        for (int x = 0 + nonObstacleOffset; x < maxCells; x++) {

            // Get current row position
            row = ((x - nonObstacleOffset) / m_NoOfCols);
            col = ((x - nonObstacleOffset) % m_NoOfCols);



            // Populates 2 dimensional array
            if (Integer.parseInt(gridArr[x]) == 1) {
                obstacleArr[row][col] = 1;
            }
            else if (Integer.parseInt(gridArr[x]) == 2) {
                obstacleArr[row][col] = 2;
            }
            else if (Integer.parseInt(gridArr[x]) == 3) {
                obstacleArr[row][col] = 3;
            } else
                obstacleArr[row][col] = 0;

        }
        return obstacleArr;
    }

    private void InitValues() {
        //Put all the default values
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        map_NoOfRows = DEFAULT_NO_ROWS;
        map_NoOfCols = DEFAULT_NO_COLS;
        map_width = DEFAULT_WIDTH;
        map_Height = DEFAULT_HEIGHT;
        map_XOffset = DEFAULT_X_OFFSET;
        map_YOffset = DEFAULT_Y_OFFSET;
    }

    public void createRobotHead(float iCol, float iRow, Canvas canvas) {

        float X = DEFAULT_X_OFFSET;
        float Y = DEFAULT_Y_OFFSET;

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(X + (iCol * map_width),
                Y + (iRow * map_Height), X + (iCol * map_width)
                        + map_width,
                Y + (iRow * map_Height) + map_Height, paint);


    }

    public void createRobotBody(float iCol, float iRow, Canvas canvas) {

        float X = DEFAULT_X_OFFSET;
        float Y = DEFAULT_Y_OFFSET;

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(X + (iCol * map_width),
                Y + (iRow * map_Height), X + (iCol * map_width)
                        + map_width,
                Y + (iRow * map_Height) + map_Height, paint);
    }

    public void createRobotPart(int[] iCol, int[] iRow, Canvas canvas) {

        float X = DEFAULT_X_OFFSET;
        float Y = DEFAULT_Y_OFFSET;


        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        for (int count = 0; count < 9; count++) {

            canvas.drawRect(X + (iCol[count] * map_width),
                    Y + (iRow[count] * map_Height), X + (iCol[count] * map_width)
                            + map_width,
                    Y + (iRow[count] * map_Height) + map_Height, paint);

        }


    }


}
