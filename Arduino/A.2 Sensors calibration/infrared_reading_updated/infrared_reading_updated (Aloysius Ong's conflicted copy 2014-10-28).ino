#include "DualVNH5019MotorShield.h"
#include <Array.h>

DualVNH5019MotorShield md;

int sensorL[5]={600, 285, 460, 350, 270};
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
  PWM_Mode_Setup();
}

void loop(){
  sendSensordata();
}


void sendSensordata(){
 
  int bin[16] = {0};
  
  
  int sensorL[5]={500, 285, 460, 350, 270};

  int sensorR[5]={530, 285, 450, 330, 240};

  
  int loopcount = 12;
  int urmreading;

  int count =0;
  int offset = 50;
  int FX[2] = {0,0};
  int FY[2] = {0,0};
  int FZ[2] = {0,0};
  int LX[6] = {0,0,0,0,0,0};
  int RX[6] = {0,0,0,0,0,0};
  Array <int> AFX = Array<int>(FX,2);
  Array <int> AFY = Array<int>(FY,2);    
  Array <int> AFZ = Array<int>(FZ,2);  
  Array <int> ALX = Array<int>(LX,6);  
  Array <int> ARX = Array<int>(RX,6);  
  
  while (count < loopcount){
    
    sensorRead[0] = analogRead(A0);
    sensorRead[1] = analogRead(A1);
    sensorRead[2] = analogRead(A2);
    sensorRead[3] = analogRead(A3);
    sensorRead[4] = analogRead(A4);
    sensorRead[5] = analogRead(A5);
    urmreading = PWM_Mode();
    //sense front x
    if ( offset > abs(sensorRead[0] - 280)){
      FX[1]++;
    }else {FX[0]++;}
    //sense front y
    if (3 > abs(urmreading - 8)){
      FY[1]++;
    }else {FY[0]++;}  
    //sense front z
    if ( offset > abs(sensorRead[3] - 630)){
      FZ[1]++;
    }else {FZ[0]++;}
    
    //sense left
    if ( 400 < abs(sensorRead[1])){
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
    if ( 400 < abs(sensorRead[4])){
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
    delay(50);
    count++;
    Serial.print("left short: ");
     Serial.print(sensorRead[4]);
      Serial.print(" long: ");
       Serial.println(sensorRead[5]);
    
  }
  
  int checkFX = AFX.getMax();
  int checkFY = AFY.getMax();
  int checkFZ = AFZ.getMax();
  int checkL = ALX.getMax();
  int checkR = ARX.getMax();
  
  if(checkFX == FX[1]){
      Serial.print("ph:FX1");
      bin[2] = 1;
    }
    
  if(checkFY == FY[1]){
      Serial.print("ph:FY1");
      bin[1] = 1;
    }
  
    if(checkFZ == FZ[1]){
      Serial.print("ph:FZ1");
      bin[0] = 1;
    }

  for(int i=1; i<6; i++){
    if(checkL == LX[i]){
      Serial.print("ph:LX");
      Serial.println(i);
      bin[12+1-i] = 1;
      break;
    }
  }
  
  for(int i=1; i<6; i++){
    if(checkR == RX[i]){
      Serial.print("ph:RX");
      Serial.println(i);
      bin[7+1-i] = 1;
      break;
    }
  }
  
  
  
 
 
 
//   
////front
//    Serial.print("front x: ");
//    Serial.print(sensorRead[0]);
//    Serial.print("  ;right y: ");
//    Serial.print(sensorRead[6]);
//    Serial.print("  ;right z: ");
//    Serial.println(sensorRead[3]);
    
    //  left
//    Serial.print("left short: ");
//    Serial.print(sensorRead[1]);
//    Serial.print("  ;left long: ");
//    Serial.println(sensorRead[2]);
// 
  
////  right
//    Serial.print("right short: ");
//    Serial.print(sensorRead[4]);
//    Serial.print("  ;right long: ");
//    Serial.println(sensorRead[5]);
    
//    Serial.println("right short: "+sensorRead[4] + " long: " +  sensorRead[5]);
//   Serial.print("2.");
//   Serial.println(sensorRead[2]);

//
//  char c[2];
//  int dec[2] = {0,0};
//  
// 
//  for (int i=7; i>=0;i--){       
//      dec[0] = dec[0] + (ceil)(bin[i]*pow(2,i));     
//      dec[1] = dec[1] + (ceil)(bin[i+8]*pow(2,i));   
//  }
  
//  c[0] = char(dec[0]);
//  c[1] = char(dec[1]);  
////  for (int i=0;i<16;i++)
////      Serial.println(bin[i]);
//  char d[] = {'H',':',c[1],c[0]};    
//  
//  for (int i=0;i<16;i++)
//      Serial.println(bin[i]);
//  Serial.println(dec[1]);
//  Serial.println(dec[0]);
//  Serial.println(d[2]);
//  Serial.println(d[3]);
//  

  
  delay(50);
 }
