#include "DualVNH5019MotorShield.h"
#include <Array.h>

void sendSensordata(){
 
  int bin[16] = {0};
  
  
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A4);
  sensorRead[5] = analogRead(A5);
  urmreading = PWM_Mode();
  
  int sensorL[5]={500, 285, 460, 350, 270};

  int sensorR[5]={600, 285, 450, 330, 240};

  
  int loopcount = 5;

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
    //sense front x
    if ( offset > abs(sensorRead[0] - 280)){
      FX[1]++;
    }else {FX[0]++;}
    //sense front y
    if (2 > abs(urmreading - 5)){
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
  
  int checkFX = AFX.getMax();
  int checkFY = AFY.getMax();
  int checkFZ = AFZ.getMax();
  int checkL = ALX.getMax();
  int checkR = ARX.getMax();
  
  if(checkFX == FX[i]){
      //Serial.print("ph:FX"+i);
      bin[2] = 1;
    }
    
  if(checkFY == FY[i]){
      //Serial.print("ph:FY"+i);
      bin[1] = 1;
    }
  
    if(checkFZ == FZ[i]){
      //Serial.print("ph:FZ"+i);
      bin[0] = 1;
    }

  for(int i=1; i<6; i++){
    if(checkL == LX[i]){
      //Serial.print("ph:LX"+i);
      bin[12+1-i] = 1;
      break;
    }
  }
  
  for(int i=1; i<6; i++){
    if(checkR == RX[i]){
      //Serial.print("ph:RX"+i);
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
//    Serial.println("left short: "+sensorRead[1] + " long: " +  sensorRead[2]);
//    Serial.println("right short: "+sensorRead[4] + " long: " +  sensorRead[5]);
//   Serial.print("2.");
//   Serial.println(sensorRead[2]);

//  int obstacle1 = 530;
//  int obstacle2 = 300;
//  int obstacle3 = 0;
//  int obstacle4 = 0;
//  int obstacle5 = 0;

//  int offset = 50;
//  
//  if ( offset > abs(sensorRead[0] - 280)){      //FX
//    bin[2] = 1;
//  }
//  
//  if(2 > abs(urmreading - 5)){                  //FY
//    bin[1] = 1;
//  }
//  
//  if ( offset > abs(sensorRead[3] - 420)){      //FZ
//  bin[0] = 1;
//  }
//  
//  
//  
//  
//  //sense left
//  if ( offset > abs(sensorRead[1] - sensorL[0])){      //LX1
//    bin[12] = 1;
//  }
//  else if (offset > abs(sensorRead[1] - sensorL[1])){    //LX2
//    bin[11] = 1;
//  }
//  else if ( offset > abs(sensorRead[2] - sensorL[2])){      //LX3
//    bin[10] = 1;
//  }
//  else if (offset-10 > abs(sensorRead[2] - sensorL[3])){      //LX4
//    bin[9] = 1;
//  }
//  else if(offset-20 > abs(sensorRead[2] - sensorL[4])){    //LX5
//    bin[8] = 1;
//  }
//  
//  
//  
//
//  
//  //sense right
//  if ( offset > abs(sensorRead[4] - sensorR[0])){            //RX1
//    bin[7] = 1;
//  }
//  else if (offset > abs(sensorRead[4] - sensorR[1])){        //RX2
//    bin[6] = 1;
//  }
//  else if ( offset > abs(sensorRead[5] - sensorR[2])){        //RX3
//    bin[5] = 1;
//  }
//  else if (offset > abs(sensorRead[5] - sensorR[3])){        //RX4
//    bin[4] = 1;
//  }
//  else if(offset-20 > abs(sensorRead[5] - sensorR[4])){      //RX5
//    bin[3] = 1;
//  }

  char c[2];
  int dec[2] = {0,0};
  
 
  for (int i=7; i>=0;i--){       
      dec[0] = dec[0] + (ceil)(bin[i]*pow(2,7-i));     
      dec[1] = dec[1] + (ceil)(bin[i+8]*pow(2,7-i));   
  }
  
  c[0] = char(dec[0]);
  c[1] = char(dec[1]);  
//  for (int i=0;i<16;i++)
//      Serial.println(bin[i]);
  char d[] = {'H',':',c[0],c[1]};    
  Serial.println(d);

  
  delay(500);
 }

