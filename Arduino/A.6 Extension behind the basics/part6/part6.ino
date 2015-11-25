#include <DualVNH5019MotorShield.h>
#include <PololuWheelEncoders.h>
#include <PID_v1.h>

DualVNH5019MotorShield motor;
PololuWheelEncoders encoders;

double Setpoint, Input, Output;
double consKp=0.4, consKi=0.1, consKd=0.2;
PID myPID(&Input, &Output, &Setpoint, consKp, consKi, consKd, DIRECT);

int M1Speed = 200;    //Left Motor
int M2Speed = 190;    //Right Motor
int sensorRead[5];
int urmreading;
int counter = 0;

void setup()
{
  encoders.init(3,5,11,13);
  Serial.begin(9600);
  motor.init();
  
  Setpoint = 0;
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-1,1);
  
  PWM_Mode_Setup();
}

void loop(){
  
  moveStraight2(10);

  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A5);
  urmreading = PWM_Mode();
  
//  for (int i=0;i<5;++i){
//    Serial.print(i);
//    Serial.print(".");
//    Serial.println(sensorRead[i]);
//  }  
  
  checknextMove();

}
 
void checknextMove()
{
  sensorRead[0] = analogRead(A0);
  sensorRead[1] = analogRead(A1);
  sensorRead[2] = analogRead(A2);
  sensorRead[3] = analogRead(A3);
  sensorRead[4] = analogRead(A5);
  
  if (sensorRead[0]>540 || sensorRead[3]>530 || urmreading<18)  //Obstacle detected in straight line
    {//md.setM1Speed(0);
     //md.setM2Speed(0);
    // delay(200);          
      if (analogRead(2) > 500)
             { motor.setM2Speed(0);  
               motor.setM1Speed(-400);
              delay(2000);}
      else 
              { motor.setM1Speed(0);  
                motor.setM2Speed(400);
               delay(2000);}
    
     }  
} 


void turnLeft()
{
 // moveStop();  
  int rot = (int)(90*1.63);  //6.18
  Serial.println("Turning left");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    motor.setM1Speed(-M1Speed);
    motor.setM2Speed(M2Speed);
    delay(5);
  } 
 // delay(100);
}


void turnRight()
{
//  moveStop();
  int rot = (int)(90*1.63);   //6.18
  Serial.println("Turning right");
  for ( int i = 0; i <= rot ; i++)  //right
  {  
    motor.setM1Speed(M1Speed);
    motor.setM2Speed(-M2Speed);
    delay(5);
  }
 // delay(100);
}


void moveStop(){
    motor.setBrakes(400,400);
    delay(1000);
}


void turnAroundlt()
{
  
  int rot = (int)(180*1.63);  //6.18
 // Serial.println("Turning 180*");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    motor.setM1Speed(-M1Speed);
    motor.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
  
}

void turnAroundrt()
{
  
  int rot = (int)(180*1.63);  //6.18
 // Serial.println("Turning 180*");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    motor.setM1Speed(M1Speed);
    motor.setM2Speed(-M2Speed);
    delay(5);
  } 
  delay(100);
   
}

