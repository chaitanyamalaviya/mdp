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

//=================================================================================//
//=============================IR variable initial=================================//
//=================================================================================//
int frontLeft/*174*/, frontRight/*290*/;
int leftShort, leftLong;
int rightShort, rightLong;
int initial_frontLeft = 0, initial_frontRight = 0;
//=================================================================================//

//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void setup() 
{
  Serial.begin(9600);
   
  motor.init();
  initialIRRead();
  
  
  //ClearEncoderCount();
  moveLeft(90);
  delay(500);
  moveLeft(90);
  delay(500);
  moveLeft(90);
  delay(500);
  moveLeft(90);
  //delay(500);
  
  updateIRRead();
//  IRcali();
//  moveRight(90);
  
 
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
  int revolutionNeeded = 14 * degree +45;
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
  int revolutionNeeded = 14 * degree + 30;
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

//===============================================================================//
//==============================IR Calibration===================================//
//===============================================================================//
void IRcali(){
  while (abs(frontLeft - initial_frontLeft) > 25 || abs(frontRight - initial_frontRight) > 25){
    Serial.println("In while looping");
    
    if (frontLeft > initial_frontLeft && frontRight < initial_frontRight){
      motor.setSpeeds(-200,200);
      delay(50);
      updateIRRead(); 
    }
    
    if (frontLeft < initial_frontLeft && frontRight > initial_frontRight){
      motor.setSpeeds(200,-200);
      delay(50);
      updateIRRead(); 
    } 
  }
}

void updateIRRead(){
  frontLeft = analogRead(A0);
  leftLong = analogRead(A1);
  leftShort = analogRead(A2);
  frontRight = analogRead(A3);
  rightLong = analogRead(A4);
  rightShort = analogRead(A5);
}

void initialIRRead(){
  initial_frontLeft = analogRead(A0);
  initial_frontRight = analogRead(A3);
}

