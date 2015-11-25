#include "DualVNH5019MotorShield.h"
#inculde <Array.h>

DualVNH5019MotorShield md;

int sensorL[5]={500, 285, 460, 350, 270};
//sensorL[0] = 500;
//sensorL[1] = 285;
//sensorL[2] = 460;
//sensorL[3] = 350;
//sensorL[4] = 270;

int sensorR[5]={600, 285, 450, 330, 240};
//sensorR[0] = 600;
//sensorR[1] = 285;
//sensorR[2] = 450;
//sensorR[3] = 330;
//sensorR[4] = 240;

int sensorRead[6];
int offset = 50;

void setup()
{
  Serial.begin(9600);
}

void loop(){
  updateSensorsRead();
    
//  //  left
//  Serial.print("left short: ");
//  Serial.print(sensorRead[1]);
//  Serial.print("  ;left long: ");
//  Serial.println(sensorRead[2]);
 
  
////  right
//    Serial.print("right short: ");
//    Serial.print(sensorRead[4]);
//    Serial.print("  ;right long: ");
//    Serial.println(sensorRead[5]);
  int loopcount = 5;

  int countFX = 0;
  int FX[2] = {0,0};
  int countFY = 0;
  int FY[2] = {0,0};
  int countFZ = 0;
  int FZ[2] = {0,0};
  int countLeftSense = 0;
  int LX[6] = {0,0,0,0,0,0};
  int countRightSense = 0;
  int RX[6] = {0,0,0,0,0,0};
  
  
  while (countFX <loopcount){
      // sense front
    if ( offset > abs(sensorRead[0] - 280)){
      FX[1]++;
      //Serial.print("ph:FX1");
    }else {
      //Serial.print("ph:FX0");
      FX[0]++;}
    countFX++;
    delay(500);
  }
  
  int checkFX = FX[].getMax();
  
  switch (checkFX){
    case FX[0]: Serial.print("ph:LX0");break;
    case FX[1]: Serial.print("ph:LX1");break;
  }
  
  while (countFY <loopcount){
      // sense front
    if ( offset > abs(sensorRead[3] - 630)){
      //Serial.print("ph:FY1");
      FY[1]++;
    }else {
      //Serial.print("ph:FY0");
      FY[0]++;
    }  
    CountFY++;
  }

  int checkFY = FY[].getMax();
  
  switch (checkFY){
    case FY[0]: Serial.print("ph:LX0");break;
    case FY[1]: Serial.print("ph:LX1");break;
  }
  
   while (countFZ <loopcount){
      // sense front
    if ( offset > abs(sensorRead[3] - 630)){
      //Serial.print("ph:FZ1");
      FZ[1]++;
    }else {
      //Serial.print("ph:FZ0");
      FZ[0]++;
    }
    countFZ++;  
    }

  int checkFZ = FZ[].getMax();
  
  switch (checkFZ){
    case FZ[0]: Serial.print("ph:LX0");break;
    case FZ[1]: Serial.print("ph:LX1");break;
  }
  
  
  
  while (countLeftSense <loopcount)){
  
  //sense left
  if ( offset > abs(sensorRead[1] - sensorL[0])){
    //Serial.print("ph:LX1");
    LX[1]++;
  }
  else if (offset > abs(sensorRead[1] - sensorL[1])){
    //Serial.print("ph:LX2");
    LX[2]++;
  }
  else if ( offset > abs(sensorRead[2] - sensorL[2])){
    //Serial.print("ph:LX3");
    LX[3]++;
  }
  else if (offset-10 > abs(sensorRead[2] - sensorL[3])){
    //Serial.print("ph:LX4");
    LX[4]++;
  }
  else if(offset-20 > abs(sensorRead[2] - sensorL[4])){
    //Serial.print("ph:LX5");
    LX[5]++;
  }
  else {
    //Serial.print("ph:LX0"); 
    LX[0]++;}  
  countLeftSense++;
  delay(500);
  }
  
  int checkL = LX[].getMax();
  
  switch (checkL){
    case LX[0]: Serial.print("ph:LX0");break;
    case LX[1]: Serial.print("ph:LX1");break;
    case LX[2]: Serial.print("ph:LX2");break;
    case LX[3]: Serial.print("ph:LX3");break;
    case LX[4]: Serial.print("ph:LX4");break;
    case LX[5]: Serial.print("ph:LX5");break;  
  }
  
    


  //sense right
  while(countRightSense < loopcount)){
  if ( offset > abs(sensorRead[4] - sensorR[0])){
    //Serial.print("ph:RX1");
    RX[1]++;
  }
  else if (offset > abs(sensorRead[4] - sensorR[1])){
    //Serial.print("ph:RX2");
    RX[2]++;
  }
  else if ( offset > abs(sensorRead[5] - sensorR[2])){
    //Serial.print("ph:RX3");
    RX[3]++;
  }
  else if (offset > abs(sensorRead[5] - sensorR[3])){
    //Serial.print("ph:RX4");
    RX[4]++;
  }
  else if(offset-20 > abs(sensorRead[5] - sensorR[4])){
    //Serial.print("ph:RX5");
    RX[5]++;
  }
  else {//Serial.print("ph:RX0");
    RX[0]++;
  }
  countRightSense++;
  delay(500);
  }
  
    int checkR = RX[].getMax();
  
  switch (checkR){
    case RX[0]: Serial.print("ph:LX0");break;
    case RX[1]: Serial.print("ph:LX1");break;
    case RX[2]: Serial.print("ph:LX2");break;
    case RX[3]: Serial.print("ph:LX3");break;
    case RX[4]: Serial.print("ph:LX4");break;
    case RX[5]: Serial.print("ph:LX5");break;  
  }
  
  delay(500);
 }

 
 void updateSensorsRead()
{
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A4);
  sensorRead[5] = analogRead(A5);
  //PWM_Mode();
}
