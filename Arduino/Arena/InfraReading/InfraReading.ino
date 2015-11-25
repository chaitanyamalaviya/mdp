
void sendSensordata(){
 
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A4);
  sensorRead[5] = analogRead(A5);
  urmreading = PWM_Mode();
  
  int sensorL[5];
  sensorL[0] = 500;
  sensorL[1] = 285;
  sensorL[2] = 460;
  sensorL[3] = 350;
  sensorL[4] = 270;
  
  int sensorR[5];
  sensorR[0] = 600;
  sensorR[1] = 285;
  sensorR[2] = 450;
  sensorR[3] = 330;
  sensorR[4] = 240;
  
  
 
 
 
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

  int obstacle1 = 530;
  int obstacle2 = 300;
  int obstacle3 = 0;
  int obstacle4 = 0;
  int obstacle5 = 0;
  int offset = 50;
//  if ( offset > abs(sensorRead[0] - 280)){
//    Serial.print("ph:FX1");
//  }
//  else {Serial.print("ph:FX0");}
//    
//  if ( offset > abs(sensorRead[3] - 630)){
//    Serial.print("ph:FY1");
//  }
//  else {Serial.print("ph:FY0");}
//  
//  if ( offset > abs(sensorRead[3] - 420)){
//    Serial.print("ph:FZ1");
//  }
//  else {Serial.print("ph:FZ0");}
//  
//  
  //sense left
  if ( offset > abs(sensorRead[1] - sensorL[0])){
    Serial.print("ph:LX1");
  }
  else if (offset > abs(sensorRead[1] - sensorL[1])){
    Serial.print("ph:LX2");
  }
  else if ( offset > abs(sensorRead[2] - sensorL[2])){
    Serial.print("ph:LX3");
  }
  else if (offset-10 > abs(sensorRead[2] - sensorL[3])){
    Serial.print("ph:LX4");
  }
  else if(offset-20 > abs(sensorRead[2] - sensorL[4])){
    Serial.print("ph:LX5");
  }
  else {Serial.print("ph:LX0");}
  
  
  if(2 > abs(urmreading - 5)){
    Serial.print("ph:FY1");
  }
  else {Serial.print("ph:FY00");}
  
//  //sense right
//  if ( offset > abs(sensorRead[4] - sensorR[0])){
//    Serial.print("ph:RX1");
//  }
//  else if (offset > abs(sensorRead[4] - sensorR[1])){
//    Serial.print("ph:RX2");
//  }
//  else if ( offset > abs(sensorRead[5] - sensorR[2])){
//    Serial.print("ph:RX3");
//  }
//  else if (offset > abs(sensorRead[5] - sensorR[3])){
//    Serial.print("ph:RX4");
//  }
//  else if(offset-20 > abs(sensorRead[5] - sensorR[4])){
//    Serial.print("ph:RX5");
//  }
//  else {Serial.print("ph:RX0");}
  
  delay(500);
 }

