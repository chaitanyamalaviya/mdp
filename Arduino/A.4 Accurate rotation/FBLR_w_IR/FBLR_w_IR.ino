#include "DualVNH5019MotorShield.h"

DualVNH5019MotorShield md;

int M1Speed = 200;    //Left Motor
int M2Speed = 190;   //Right Motor
int IRDist = 500;

void setup()
{
  Serial.begin(9600);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
}
  
//void stopIfFault()
//{
//  if (md.getM1Fault())
//  {
//    Serial.println("M1 fault");
//    while(1);
//  }
//  if (md.getM2Fault())
//  {
//    Serial.println("M2 fault");
//    while(1);
//  }
//}

void turnLeft(int degree)
{
  moveStop();  
  int rot = (int)(degree*1.6);  //6.18
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
}

void turnRight(int degree)
{
  moveStop();
  int rot = (int)(degree*1.6);   //6.18
  for ( int i = 0; i <= rot ; i++)  //right
  {  
    md.setM1Speed(M1Speed);
    md.setM2Speed(-M2Speed);
    delay(5);
  }
  delay(100);
}

void moveForward(){  //forward
  md.setSpeeds(M1Speed,M2Speed);
  delay(10);
}

void moveReverse(){  //reverse
    md.setSpeeds(M1Speed,-M2Speed);
    delay(10);
}

void moveStop(){
    md.setBrakes(400,400);
    delay(1000);
}

void loop(){
//  int sensorRead[5];
//  sensorRead[0] = analogRead(A0);
//  sensorRead[1] = analogRead(A1);
//  sensorRead[2] = analogRead(A2);
//  sensorRead[3] = analogRead(A3);
//  sensorRead[4] = analogRead(A4);
  
  turnLeft(720);
  turnRight(720); 
  //moveForward();
  /*
    if ((sensorRead[0] >= IRDist) || (sensorRead[2] >= IRDist) ) {
      if (sensorRead[3] >= IRDist) {
          if (sensorRead[1] >= IRDist) moveReverse();
          turnRight(); 
      }
      else if (sensorRead[1] >= IRDist) turnLeft();
      else turnLeft();
    }
    else 
    {  
      moveForward();
    }
    */
}

