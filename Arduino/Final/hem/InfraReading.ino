#include "DualVNH5019MotorShield.h"
#include <Array.h>

int count;
char c[2];
int loopcount = 12, offset = 50;

int sensorL[5]={500, 285, 460, 350, 270};
int sensorR[5]={600, 285, 450, 330, 240};

void sendSensordata(){
  count=0;
  int FX[2] = {0,0};
  int FY[2] = {0,0};
  int FZ[2] = {0,0};
  int LX[6] = {0,0,0,0,0,0};
  int RX[6] = {0,0,0,0,0,0};
  
  int dec[2] = {0,0};
  int bin[16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
  
  Array <int> AFX = Array<int>(FX,2);
  Array <int> AFY = Array<int>(FY,2);    
  Array <int> AFZ = Array<int>(FZ,2);  
  Array <int> ALX = Array<int>(LX,6);  
  Array <int> ARX = Array<int>(RX,6);
  //delay(500);  
  
  while (count < loopcount){
    
    sensorRead[1] = analogRead(A1);//Short Range Left Sensor
    sensorRead[2] = analogRead(A2);//Long Range Left Sensor
    
    sensorRead[0] = analogRead(A0);//Front X Sensor (Short Range)
    urmreading = PWM_Mode(); //Front Y Sensor (Ultra Sonic)
    sensorRead[3] = analogRead(A3);//Front Z Sensor (Short Range)
    
    sensorRead[4] = analogRead(A4);//Short Range Right Sensor
    sensorRead[5] = analogRead(A5);//Long Range Right Sensor
    
    
    //sense front x
    if (450 < abs(sensorRead[0])){
      FX[1]++;
    } else {
      FX[0]++;
    }
    
    //sense front y
    if (3 > abs(urmreading -8)){
      FY[1]++;
    }else {
      FY[0]++;
    }
    
    //sense front z
    if (450 < abs(sensorRead[3])){
      FZ[1]++;
    }else {
      FZ[0]++;
    }
    
    //sense left
    if (offset > abs(sensorRead[1] - sensorL[0])){
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
      LX[0]++;
    }  
      
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
    delay(80);
    count++;
  }
  //
  int checkFX = AFX.getMax();
  int checkFY = AFY.getMax();
  int checkFZ = AFZ.getMax();
  int checkL = ALX.getMax();
  int checkR = ARX.getMax();
  
  if(checkFX == FX[1]){
    bin[2] = 1;
  }
    
  if(checkFY == FY[1]){
    bin[1] = 1;
  }
  
  if(checkFZ == FZ[1]){
    bin[0] = 1;
  }
    
  //sense right
  if ( checkR == RX[1] ){            //RX1
    bin[7] = 1;
  }
  else if (checkR == RX[2]){        //RX2
    bin[6] = 1;
  }
  else if ( checkR == RX[3]){        //RX3
    bin[5] = 1;
  }
  else if (checkR == RX[4]){        //RX4
    bin[4] = 1;
  }
  else if(checkR == RX[5]){      //RX5
    bin[3] = 1;
  }
  
  //Left
  if ( checkL == LX[1]){      //LX1
    bin[12] = 1;
  }
  else if (checkL == LX[2]){    //LX2
    bin[11] = 1;
  }
  else if (checkL == LX[3]){      //LX3
    bin[10] = 1;
  }
  else if (checkL == LX[4]){      //LX4
    bin[9] = 1;
  }
  else if(checkL == LX[5]){    //LX5
    bin[8] = 1;
  }
  
  Serial.print("H:");  
  for(int i = 15; i>=0; i--){
    Serial.print(bin[i]);
  }
  Serial.println("");
  delay(100);
 }

