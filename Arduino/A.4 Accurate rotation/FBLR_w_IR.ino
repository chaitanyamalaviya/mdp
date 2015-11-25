#include "DualVNH5019MotorShield.h"

DualVNH5019MotorShield md;

int M1Speed = 330;    //Left Motor
int M2Speed = 320;   //Right Motor
int IRDist = 500;

void setup()
{
  Serial.begin(9600);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
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
  for ( int i = 0; i <= 155; i++)  //left
  {  
    md.setM1Speed(M1Speed);
    md.setM2Speed(M2Speed);
    delay(2);
  } 
  delay(10);
}

void turnRight()
{
  moveStop();
  for ( int i = 0; i <= 155; i++)  //right
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(-M2Speed);
    delay(2);
  }
  delay(10);
}

void moveForward(){  //forward
  md.setSpeeds(-M1Speed,M2Speed);
  delay(10);
}

void moveReverse(){  //reverse
    md.setSpeeds(M1Speed,-M2Speed);
    delay(10);
}

void moveStop(){
    md.setBrakes(400,400)
}

void loop(){
  int sensorRead[5];
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A4);
  
   printIR();  
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
}

