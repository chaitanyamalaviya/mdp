#include <PID_v1.h>
#include <Encoder.h>
#include <DualVNH5019MotorShield.h>
#include <Wire.h>

double Setpoint, Input, Output;

double aggKp=4, aggKi=0.2, aggKd=1;
double consKp=1, consKi=0.05, consKd=0.25;

PID myPID(&Input, &Output, &Setpoint, consKp, consKi, consKd, DIRECT);

int leftSpeed = 300;
int rightSpeed = 300;

DualVNH5019MotorShield motor;

Encoder knobLeft(3, 5);
Encoder knobRight(11, 13);

long leftCount = 0;
long rightCount = 0;
//int LRerror;

int kp=80;

//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void setup(){
  Serial.begin(9600);
  Serial.println("Counting start...");
  motor.init();
  ClearEncoderCount();
 
  myPID.SetMode(AUTOMATIC);
  
  moveForward(500);         //walk for 500*12=6000mm
  
  Serial.print("LeftCount = ");
  Serial.println(knobLeft.read());
  Serial.print("RightCount = ");
  Serial.println(knobRight.read());
}

void loop(){
  delay(20);
}

void moveForward(int dist)
{
   int revolutionNeeded = dist * 12;
   int totalRevolution = 0;
   Setpoint = 0;   
   
   while (knobLeft.read() < revolutionNeeded)
   {
     Input = knobLeft.read() - knobRight.read();

     motor.setSpeeds(leftSpeed, rightSpeed);
     
     GetEncoderCount();
     Input = knobLeft.read() - knobRight.read();
     myPID.SetTunings(consKp, consKi, consKd);
     myPID.Compute();
     Serial.print("Left = ");
     Serial.print(knobLeft.read());
     Serial.print("Right = ");
     Serial.println(knobRight.read());
     Serial.println(Input);
//     Serial.println(Output);
     rightSpeed = rightSpeed + Output;
   }
   motor.setBrakes(400,400);
}
     
void GetEncoderCount()
{
  leftCount = knobLeft.read();
  rightCount = knobRight.read();
}

void ClearEncoderCount()
{
  knobLeft.write(0);
  knobRight.write(0);
  leftCount = 0;
  rightCount = 0;
}
