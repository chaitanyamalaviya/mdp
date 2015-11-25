#include <DualVNH5019MotorShield.h>
#include <Wire.h>
#include <Encoder.h>
#include <math.h>

int masterspeed = 200;
int slavespeed = 200;

//float preDistance;

DualVNH5019MotorShield motor;

Encoder knobLeft(3, 5);
Encoder knobRight(11, 13);

int left_count = 0;
int right_count = 0;
int LRerror = 0;

int kp = 80; //constant of proportionality
//kp = 80 for speed 400

//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void setup() 
{
  motor.init();
  //ClearEncoderCount();
  Serial.begin(9600);
  moveStraight(1500);
  
  
  Serial.println("Encoder Count Check:");
  Serial.print("Left = ");
  Serial.print(knobLeft.read());
  Serial.print(", Right = ");
  Serial.print(knobRight.read());
  Serial.println();
}

void loop()
{
  //checkRight();
}

void moveStraight(int distance)
{
  //distance in mm
  int revolutionNeeded = 12 * distance;
  
  //slavespeed = masterspeed - 5;
  
  ClearEncoderCount();
  
  while(knobLeft.read() < revolutionNeeded)
  {
    Serial.print("RightCount");
    Serial.println(right_count);
    Serial.print("LeftCount");
    Serial.println(left_count);
    //masterspeed = (masterspeed%401);
    //slavespeed = (slavespeed%401);
    motor.setSpeeds(masterspeed, slavespeed);

    GetEncoderCount();
    LRerror = left_count - right_count;
    slavespeed += (LRerror / kp);
    
   Serial.print("M1 Speed ");
   Serial.println(masterspeed);
   Serial.print("M2 Speed ");
   Serial.println(slavespeed);
    //ClearEncoderCount();
  }
   motor.setSpeeds(0, 0);
}

void GetEncoderCount()
{
  left_count = knobLeft.read();
  right_count = knobRight.read();
}

void ClearEncoderCount()
{
  knobLeft.write(0);
  knobRight.write(0);
  left_count = 0;
  right_count = 0;
}

/*void checkRight()
{
  float curDistance = 12343.85 * pow(analogRead(0),-1.15);
  if (curDistance - preDistance > 3){
     masterspeed = masterspeed * 1.02;
     preDistance = curDistance;
  }
  else if (curDistance - preDistance < -3){
    slavespeed = slavespeed * 1.02;
    preDistance = curDistance;
  }
}*/
