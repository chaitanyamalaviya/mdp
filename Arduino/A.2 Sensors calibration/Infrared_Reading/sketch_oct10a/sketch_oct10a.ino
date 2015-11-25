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

void sense(){
  updateSensorsRead();
  int loopcount = 5;

  int count =0;
  int FX[2] = {0,0};
  int FY[2] = {0,0};
  int FZ[2] = {0,0};
  int LX[6] = {0,0,0,0,0,0};
  int RX[6] = {0,0,0,0,0,0};
  while (count < loopcount){
    //sense front x
    if ( offset > abs(sensorRead[0] - 280)){
      FX[1]++;
    }else {FX[0]++;}
    //sense front y
    if ( offset > abs(sensorRead[3] - 630)){
      FY[1]++;
    }else {FY[0]++;}  
    //sense front z
    if ( offset > abs(sensorRead[3] - 630)){
      FZ[1]++;
    }else {FZ[0]++;}
    
    //sense left
    if ( offset > abs(sensorRead[1] - sensorL[0])){
      LX[1]++;
    }
    else if (offset > abs(sensorRead[1] - sensorL[1])){
      LX[2]++;
    }
    else if ( offset > abs(sensorRead[2] - sensorL[2])){
      LX[3]++;
    }
    else if (offset-10 > abs(sensorRead[2] - sensorL[3])){
      LX[4]++;
    }
    else if(offset-20 > abs(sensorRead[2] - sensorL[4])){
      LX[5]++;
    }
    else {
      LX[0]++;}  
      
    //sense right
    if ( offset > abs(sensorRead[4] - sensorR[0])){
      RX[1]++;
    }
    else if (offset > abs(sensorRead[4] - sensorR[1])){
      RX[2]++;
    }
    else if ( offset > abs(sensorRead[5] - sensorR[2])){
      RX[3]++;
    }
    else if (offset > abs(sensorRead[5] - sensorR[3])){
      RX[4]++;
    }
    else if(offset-20 > abs(sensorRead[5] - sensorR[4])){
      RX[5]++;
    }
    else {
      RX[0]++;
    }
    
  }
  
  
  int checkFX = FX[].getMax();
  
  switch (checkFX){
    case FX[0]: Serial.print("ph:LX0");break;
    case FX[1]: Serial.print("ph:LX1");break;
  }
  

  int checkFY = FY[].getMax();
  
  switch (checkFY){
    case FY[0]: Serial.print("ph:LX0");break;
    case FY[1]: Serial.print("ph:LX1");break;
  }

  int checkFZ = FZ[].getMax();
  
  switch (checkFZ){
    case FZ[0]: Serial.print("ph:LX0");break;
    case FZ[1]: Serial.print("ph:LX1");break;
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
  
    
 int checkR = RX[].getMax();
  
  switch (checkR){
    case RX[0]: Serial.print("ph:LX0");break;
    case RX[1]: Serial.print("ph:LX1");break;
    case RX[2]: Serial.print("ph:LX2");break;
    case RX[3]: Serial.print("ph:LX3");break;
    case RX[4]: Serial.print("ph:LX4");break;
    case RX[5]: Serial.print("ph:LX5");break;  
  }
  
  
}


void loop(){
  
    
  
  
  
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
