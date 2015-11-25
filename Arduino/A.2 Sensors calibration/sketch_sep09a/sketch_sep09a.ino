#include "DualVNH5019MotorShield.h"

DualVNH5019MotorShield md;

int M1Speed = 330;    //Left Motor
int M2Speed = 320;    //Right Motor

void setup()
{
  Serial.begin(9600);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
  PWM_Mode_Setup();
}

void loop(){
  int sensorRead[4];

  moveForward();
  
  //front left 
  sensorRead[0] = analogRead(A2);
  //left
  sensorRead[1] = analogRead(A3);
  //front rigbt
  sensorRead[2] = analogRead(A4);
  //right
  sensorRead[3] = analogRead(A5);
//  if (sensorRead[0]<300 || sensorRead[2]<300)
//    {md.setM1Speed(0);
//     md.setM2Speed(0);
//     delay(1000);                 }
//  
  for (int i=0;i<4;++i){
    Serial.print(i);
    Serial.print(".");
    Serial.println(sensorRead[i]);
  }
  
  checkLeft();
  checkRight();
  
  
  
  PWM_Mode();
  delay(2000);

  Serial.flush();
}


void checkLeft(){
    if (sensorRead[1] > 200){
      turnLeft();
      PWM_MODE();
      delay(2000);
      turnRight();
    }
  }
  void checkRight(){
    if (sensorRead[3] > 200){
      turnRight();
      PWM_MODE();
      delay(2000);
      turnLeft();
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
  Serial.println("Start");
  for ( int i = 0; i <= 155; i++)  //spin
  {  
    md.setM1Speed(M1Speed);
    md.setM2Speed(M2Speed);
    delay(2);
  } 
}

void turnRight()
{
  Serial.println("Start");
  for ( int i = 0; i <= 155; i++)  //spin
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(-M2Speed);
    delay(2);
  } 
}



void moveForward() {
  md.setSpeeds(-M1Speed,M2Speed);
}

