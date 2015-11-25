// INFRARED
int sensorL[5]={500, 285, 460, 350, 270};
int sensorR[5]={600, 285, 450, 330, 240};
int sensorRead[6];
int offset = 50;

//ULTRASONIC
int URPWM = 6; // PWM Output 0－25000US，Every 50US represent 1cm
int URTRIG= 12; // PWM trigger pin
unsigned int Distance=0;
uint8_t EnPwmCmd[4]={0x44,0x02,0xbb,0x01}; 



void setup(){
    Serial.begin(9600);
    PWM_Mode_Setup();
}

void loop(){
  updateSensorsRead();
  checkObstacles();
  delay(1000);
}


//UPDATE THE SENSORS READING
void updateSensorsRead()
{
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A4);
  sensorRead[5] = analogRead(A5);
  PWM_Mode();
}

//ULTRASONIC
void PWM_Mode_Setup(){ 
  pinMode(URTRIG,OUTPUT);                     // A low pull on pin COMP/TRIG
  digitalWrite(URTRIG,HIGH);                  // Set to HIGH
  
  pinMode(URPWM, INPUT);                      // Sending Enable PWM mode command
  
  for(int i=0;i<4;i++){
      Serial.write(EnPwmCmd[i]);
   } 
}

//ULTRASONIC
void PWM_Mode(){                              // a low pull on pin COMP/TRIG  triggering a sensor reading
    digitalWrite(URTRIG, LOW);
    digitalWrite(URTRIG, HIGH);               // reading Pin PWM will output pulses
     
    unsigned long DistanceMeasured=pulseIn(URPWM,LOW);
     
    Distance=DistanceMeasured/50;           // every 50us low level stands for 1cm
   
}

void checkLeft(){
  //sense left
  if ( offset > abs(sensorRead[1] - sensorL[0])){
    Serial.write("PH:L1");
  }
  else if (offset > abs(sensorRead[1] - sensorL[1])){
    Serial.write("PH:L2");
  }
  else if ( offset > abs(sensorRead[2] - sensorL[2])){
    Serial.write("PH:L3");
  }
  else if (offset-10 > abs(sensorRead[2] - sensorL[3])){
    Serial.write("PH:L4");
  }
  else if(offset-20 > abs(sensorRead[2] - sensorL[4])){
    Serial.write("PH:L5");
  }
  else {Serial.write("PH:L0");}
}

void checkFront(){
    if ( offset > abs(sensorRead[0] - 280)){
    Serial.write("FX1");
  }
  else {Serial.write("FX0");}
    
  if ( Distance < 10){
    Serial.write("FY1");
  }
  else {Serial.write("FY0");}
  
  if ( offset > abs(sensorRead[3] - 420)){
    Serial.write("FZ1");
  }
  else {Serial.write("FZ0");}
}

void checkRight(){
  //sense right
  if ( offset > abs(sensorRead[4] - sensorR[0])){
    Serial.write("R1");
  }
  else if (offset > abs(sensorRead[4] - sensorR[1])){
    Serial.write("R2");
  }
  else if ( offset > abs(sensorRead[5] - sensorR[2])){
    Serial.write("R3");
  }
  else if (offset > abs(sensorRead[5] - sensorR[3])){
    Serial.write("R4");
  }
  else if(offset-20 > abs(sensorRead[5] - sensorR[4])){
    Serial.write("R5");
  }
  else {Serial.write("R0");}
}
  
void checkObstacles(){
  checkLeft;
  checkFront;
  checkRight;
}
