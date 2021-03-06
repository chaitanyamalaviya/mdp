#include "DualVNH5019MotorShield.h"
#include <PololuWheelEncoders.h>
#include <Array.h>

DualVNH5019MotorShield md;
PololuWheelEncoders encoders;

int M1Speed = 200;    //Left Motor
int M2Speed = 190;    //Right Motor
int sensorRead[6];
int urmreading;

//===================MoveStraight.ino==========================//
int initial_frontLeft = 548;
int initial_frontRight = 556;
int offseta = 35;
int frontLeft, frontRight;
int preError = 0;
//=============================================================//

char inChar; // Where to store the character read

boolean valex = false;

void setup()
{
  
  encoders.init(3,5,11,13);
  Serial.begin(9600);
  md.init();  
  PWM_Mode_Setup();
  
//  if (valexplore)
//    {  valfp = fastestPath();                        //back2start
//       if (valfp)                                
//            Serial.println("Back at Start");     
//    }            
//    
//  valfp = false;  
//  valfp = fastestPath();                            //start2goal
//  if (valfp)
//      Serial.println("Mission complete!");


}


void loop(){  
    
  if (explore())
      fastestPath();
  delay(200);
}

boolean explore(){
  
  int index = 0;
  char inData[4];
  inChar = '\0';
  inData[0] = '\0';
  inData[2] = '\0';
   
//  Serial.println("Exploration");
  
  while (Serial.available() > 0){
           if (index<3){
           inChar = Serial.read(); // Read a character
           inData[index] = inChar; // Store it
           index++; // Increment where to write next
           inData[index] = '\0'; // Null terminate the string                   
         }
    } // Read a character
 
// Serial.println(inData);
  
   if (inData[2]=='X')
      {  sendSensordata();
         return false;}
   
   else if (inData[0]!='\0')   
    { 
   
       if (explorenextMove(inData))
       {
             sendSensordata();        
             return false;
       }
       else
         {// Serial.println("Exploration complete"); 
           return true;                                 
         }          
    }
//  delay(500);
//  explore();

//  Serial.flush();

}


boolean explorenextMove(char arg[])
{ 
  switch(arg[2])
  {  
    
    case 'F': moveStraight(10);
              return true;
              break;
    case 'L': turnLeft();
              return true;
              break;
    case 'R': turnRight();
              return true;
              break;
    case 'B': turnAround();
              return true;
              break;          
    case 'S': moveStop();
              return false;
              break;
    default : //Serial.println("Invalid command");    
              return true;    
  };
  
  
}


void fastestPath()
{
  static int checkfp = 0;
  char inData2[40];
  
  if (checkfp==2)
      { moveStop();
        delay(1000000);
      }

  inData2[0] = '\0';
  inData2[2] = '\0';
  int index = 0, i=0; 
   
    while (Serial.available() > 0){
   
      if (index<40) {
           inChar = Serial.read(); // Read a character
           inData2[index] = inChar; // Store it
           index++; // Increment where to write next
           inData2[index] = '\0'; // Null terminate the string                             
         }
    }

  //Serial.println(inData2);
  i=2;
  
  while (inData2[i]!='\0'){ 
    
    switch(inData2[i])
    {    
    case 'F': moveStraight(10);
              break;
    case 'L': turnLeft(); 
              break;
    case 'R': turnRight();
              break;
    case 'B': turnAround();
              break;          
    case 'S': moveStop();
              checkfp++;
              break;
    default : break;//Serial.println("Invalid command");
    };
  
    i++;
  
   }
  delay(200);
  fastestPath();
}

void turnLeft()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  
  int degree = 90;
  int power = 200;
  int tickGoal =(double) degree*930/53 + (degree/90)*10;

  md.setSpeeds(-power, power-18);

  while (encoders.getCountsM1() > -tickGoal || encoders.getCountsM2() < tickGoal)
  {
    if (encoders.getCountsM1() <-tickGoal/2)
      md.setSpeeds(-power/2,power/2-9);

    if (encoders.getCountsM2() > tickGoal) 
      md.setM2Brake(400);

    if (encoders.getCountsM1() < -tickGoal)
      md.setM1Brake(400);
  }
  md.setBrakes(400,400);
  delay(100);
  checkWallAndAlign();
}


void turnRight()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  
  int degree = 90;
  int power = 200;
  int tickGoal = (double) degree*930/53 + (degree/90)*10;

  md.setSpeeds(power, -power+18);

  while (encoders.getCountsM2() > -tickGoal || encoders.getCountsM1() < tickGoal)
  {
    if (encoders.getCountsM1() > tickGoal/2)
      md.setSpeeds(power/2, -power/2+9);

    if (encoders.getCountsM2() < -tickGoal) 
      md.setM2Brake(400);

    if (encoders.getCountsM1() > tickGoal)
      md.setM1Brake(400);
  }
  md.setBrakes(400,400);
  delay(100);
  checkWallAndAlign();
}

void turnAround()
{
  turnLeft();
  turnLeft();
}

void moveStop(){
    md.setBrakes(400,400);
    delay(100);
}
//
//static int freeRam () {
//  extern int __heap_start, *__brkval; 
//  int v; 
//  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);     
//}
//

