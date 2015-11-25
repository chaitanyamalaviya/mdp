package com.mdp.grp2.batmobot;

import android.content.Context;

import java.lang.reflect.Array;

/**
 * Created by Cheng Shiang on 17/9/2014.
 */
public class RobotPosition{
    Context context;

    public static int[] robothead = new int[]{0,1}, robotcentre = new int[]{0,0};

    // Constructor
    public RobotPosition(Context context){
        this.context = context;
    }

    public static String headLocation(){
        String pos = "";

        if (robothead[1] - robotcentre[1] == 1){
            pos = "north";
        }else if(robothead[1] - robotcentre[1] == -1){
            pos = "south";
        }else if (robothead[0] - robotcentre[0] == 1){
            pos = "east";
        }else{
            //head[0] - centre[1] ==1
            pos = "west";
        }

        return pos;
    }
    public void routeLeft(){
        if (headLocation() == "north"){
            robothead[0] -= 1;
            robothead[1] -= 1;
        }else if (headLocation() == "south"){
            robothead[0] += 1;
            robothead[1] += 1;
        }else if (headLocation() == "east"){
            robothead[0] -= 1;
            robothead[1] += 1;
        }else{
            robothead[0] += 1;
            robothead[1] -= 1;
        }
    }

    public void routeRight(){
        if (headLocation() == "north"){
            robothead[0] += 1;
            robothead[1] -= 1;
        }else if (headLocation() == "south"){
            robothead[0] -= 1;
            robothead[1] += 1;
        }else if (headLocation() == "east"){
            robothead[0] -= 1;
            robothead[1] -= 1;
        }else{
            robothead[0] += 1;
            robothead[1] += 1;
        }
    }

    public void moveUp(){
        if (headLocation() == "north"){
            robothead[1] += 1;
            robotcentre[1] += 1;
        }else if (headLocation() == "south"){
            robothead[1] -= 1;
            robotcentre[1] -= 1;
        }else if (headLocation() == "east"){
            robothead[0] += 1;
            robotcentre[0] += 1;
        }else{
            robothead[0] -= 1;
            robotcentre[0] -= 1;
        }
    }

    public void moveDown(){
        if (headLocation() == "north"){
            robothead[1] -= 1;
            robotcentre[1] -= 1;
        }else if (headLocation() == "south"){
            robothead[1] += 1;
            robotcentre[1] += 1;
        }else if (headLocation() == "east"){
            robothead[0] -= 1;
            robotcentre[1] -= 1;
        }else{
            robothead[0] += 1;
            robotcentre[1] += 1;
        }
    }

}
