package com.mdp.grp2.batmobot;

/**
 * Created by ummeaiman on 18/9/2014.
 */
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Robot {

    RelativeLayout riv;
    Context context;

    // Used for formulating movements based on cellWidth
    private int cellWidth = 25;
    // Used for formulating turning degrees
    private final int turnDegree = 90;

    // With respect to beginning image direction; Facing West
    private final int southWest = 315;
    private final int south = 270;
    private final int southEast = 225;
    private final int east = 180;
    private final int northEast = 135;
    private final int north = 90;
    private final int northWest = 45;
    private final int west = 0;

    private View robotView;

    // Constructor
    public Robot(Context context, RelativeLayout rl){
        this.riv = rl;
        this.context = context;

        // rl contains View that is used for robot image
        // Initalizes Robot View
        this.robotView = this.riv.getChildAt(0);
    }

    public void createDefaultRobot(int posX, int posY, int direction){

        riv.removeAllViewsInLayout();

        int toGo = 0;

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.robot_fuller2);

        // Sets direction degree
        // Case corresponds to the pre-set directions with robot
        // In anti-clockwise motion, 0 being East through 7 being South East
        switch(direction){

            case 0:
                toGo = east;
                break;
            case 1:
                toGo = northEast;
                break;
            case 2:
                toGo = north;
                break;
            case 3:
                toGo = northWest;
                break;
            case 4:
                toGo = west;
                break;
            case 5:
                toGo = southWest;
                break;
            case 6:
                toGo = south;
                break;
            case 7:
                toGo = southEast;
                break;

        }

        // Changes image to the respective direction
        imageView.setRotation(toGo);

        // Set the size of image
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);

        // Position the image
        params.leftMargin = (posX * cellWidth);
        params.topMargin = (posY * cellWidth);

        riv.addView(imageView,params);

    }


    public void moveForward(){

        // Gets Current Layout Parameter of Robot
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) robotView.getLayoutParams();

        // Condition based on the direction robot is facing and moves forward respectively
        if(robotView.getRotation() == west){
            lp.setMargins(lp.leftMargin - cellWidth, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == east){
            lp.setMargins(lp.leftMargin + cellWidth, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == north){
            lp.setMargins(lp.leftMargin, lp.topMargin - cellWidth, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == south){
            lp.setMargins(lp.leftMargin, lp.topMargin + cellWidth, lp.rightMargin, lp.bottomMargin);
        }

        // Implements new layout parameters
        robotView.setLayoutParams(lp);

    }

    public void moveBackwards(){

        // Gets Current Layout Parameter of Robot
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) robotView.getLayoutParams();

        // Condition based on the direction robot is facing and moves forward respectively
        if(robotView.getRotation() == west){
            lp.setMargins(lp.leftMargin + cellWidth, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == east){
            lp.setMargins(lp.leftMargin - cellWidth, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == north){
            lp.setMargins(lp.leftMargin, lp.topMargin + cellWidth, lp.rightMargin, lp.bottomMargin);
        }else if (robotView.getRotation() == south){
            lp.setMargins(lp.leftMargin, lp.topMargin - cellWidth, lp.rightMargin, lp.bottomMargin);
        }

        // Implements new layout parameters
        robotView.setLayoutParams(lp);

    }


    public void turnRight(){

        // Sets view to rotate clockwise on its center
        robotView.setRotation(robotView.getRotation() + this.turnDegree);

    }

    public void turnLeft(){

        // Sets view to rotate anti-clockwise on its center
        robotView.setRotation(robotView.getRotation() - this.turnDegree);

    }

}

