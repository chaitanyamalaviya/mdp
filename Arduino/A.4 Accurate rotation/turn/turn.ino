#include <DualVNH5019MotorShield.h>
#include <PololuWheelEncoders.h>
#include <Wire.h>
#include <Encoder.h>

PololuWheelEncoders encoder;
DualVNH5019MotorShield motor;

Encoder knobLeft(3, 5);
Encoder knobRight(11, 13);

long left_count = 0;
long right_count = 0;

//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void setup() 
{
  motor.init();
  //ClearEncoderCount();
  moveLeft(90);
  delay(500);
  moveLeft(90);
  delay(500);
  moveLeft(90);
  delay(500);
  moveLeft(90);
  delay(500);
//  moveRight(90);
  
  Serial.begin(9600);
  Serial.println("Encoder Count Check:");
  Serial.print("Left = ");
  Serial.print(knobLeft.read());
  Serial.print(", Right = ");
  Serial.print(knobRight.read());
  Serial.println();
}

void loop(){}

void moveLeft(int degree)
{
  //distance in mm
  int revolutionNeeded = 14 * degree +2;
  int totalRevolution = 0;
  // 500 count 50 
  int masterspeed = 200;
  int slavespeed = -200; 
  
  ClearEncoderCount();
  
  while(abs(knobLeft.read()) < revolutionNeeded)
  {
    motor.setSpeeds(masterspeed, slavespeed);

    //GetEncoderCount();
    //ClearEncoderCount();
  }
  
   motor.setSpeeds(0, 0);
   
}

void moveRight(int degree)
{
  //distance in mm
  int revolutionNeeded = 14 * degree +2;
  int totalRevolution = 0;
  // 500 count 50 
  
  int masterspeed = -200;
  int slavespeed = 200;
  
  ClearEncoderCount();
  
  while(abs(knobLeft.read()) < revolutionNeeded)
  {
    motor.setSpeeds(masterspeed, slavespeed);

    //GetEncoderCount();
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

