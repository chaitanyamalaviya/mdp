#include "DualVNH5019MotorShield.h"
#include <PololuWheelEncoders.h>

DualVNH5019MotorShield md;
PololuWheelEncoders encoders;

int M1Speed = 200;    //Left Motor
int M2Speed = 190;    //Right Motor
int sensorRead[5];
int urmreading;
int counter = 0;

void setup()
{
  encoders.init(3,5,11,13);
  Serial.begin(9600);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
  
//  PWM_Mode_Setup();
}

void loop(){
 
  if (counter==15)
    return;
  
  moveStraight(10);
  counter++;
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A5);
  for (int i=0;i<5;++i){
    Serial.print(i);
    Serial.print(".");
    Serial.println(sensorRead[i]);
  }
  
  //urmreading = PWM_Mode();
  
 //  turnLeft();
  checknextMove();

  Serial.flush();
}
 
void checknextMove()
{
  
  if (sensorRead[0]>540 || sensorRead[2]>550 || sensorRead[4]>400)  //Obstacle detected in straight line
    {md.setM1Speed(0);
     md.setM2Speed(0);
     delay(200);          
     if (analogRead(1) > 400 )
       { turnLeft();
         moveStraight(25);
         turnRight();
         moveStraight(40);
         turnRight();
         moveStraight(20);
         turnLeft(); 
       }
      else 
       {  turnRight();  
          moveStraight(25);
          turnLeft();
          moveStraight(40);
          turnLeft();
          moveStraight(20);
          turnRight(); 
       }
       counter=counter+4;
     }  
} 

void stopIfFault()
{
  if (md.getM1Fault())
  {
    Serial.println("M1 fault");
    while(1);
  }
  if (md.getM2Fault())
  {
    Serial.println("M2 fault");
    while(1);
  }
}

void turnLeft()
{
  moveStop();  
  int rot = (int)(90*1.63);  //6.18
  Serial.println("Turning left");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
}


void turnRight()
{
  moveStop();
  int rot = (int)(90*1.63);   //6.18
  Serial.println("Turning right");
  for ( int i = 0; i <= rot ; i++)  //right
  {  
    md.setM1Speed(M1Speed);
    md.setM2Speed(-M2Speed);
    delay(5);
  }
  delay(100);
}


void moveStop(){
    md.setBrakes(400,400);
    delay(1000);
}


