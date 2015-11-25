#include <DualVNH5019MotorShield.h>
#include <PololuWheelEncoders.h>
#include <PID_v1.h>

////////////////////////////////
//Function in this file
//1.turnLeft(degree, power)      ***calibrated
//2.turnRight(degree, power)     ***calibrated
//3.moveStraight(dist, power)  :simply using ticks
//4.moveStraight2(dist)  :using PID
//  a.Accerelation() (haven't implemented)
//  b.Decerelation() (haven't implemented)
//5.resetEncoderCount()  :reset encoders count
//
/////////////////////////////////

DualVNH5019MotorShield motor;
PololuWheelEncoders encoders;

//////////movSstraight w PID///////////////////////
double Setpoint, Input, Output;
double Setpoint_AD, Input_AD, Output_AD;
double Setpoint_STEP, Input_STEP, Output_STEP;

double aggKp=0.001, aggKi=3.5, aggKd=0.01;
double consKp=0.4, consKi=0.1, consKd=0.2;
double stepKp=0.3, stepKi=0.1, stepKd=0.1;

PID myPID(&Input, &Output, &Setpoint, consKp, consKi, consKd, DIRECT);
//PID myPID_AD(&Input_AD, &Output_AD, &Setpoint_AD, aggKp, aggKi, aggKd, DIRECT);
//PID myPID_STEP(&Input_STEP, &Output_STEP, &Setpoint_STEP, stepKp, stepKi, stepKd, DIRECT);
///////////////////////////////////////////////

void setup()
{
  motor.init();
  encoders.init(3,5,11,13);
  Serial.begin(9600);

//  moveStraight(10,200);
//  delay(500);
//  moveStraight(10,200);
//  delay(500);
//  moveStraight(10,200);
//  delay(500);
//  moveStraight(10,200);
//  delay(500);
 
////////////movSstraight w PID///////////////////////
  Input = encoders.getCountsM1() - encoders.getCountsM2();
  Setpoint = 0;
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-1,1);
//  
//  Input_STEP = encoders.getCountsM1() - encoders.getCountsM2();
//  Setpoint_STEP = 0;
//  myPID_STEP.SetMode(AUTOMATIC);
//  myPID_STEP.SetOutputLimits(-10, 10);
//  
//  Setpoint_AD = 200;
//  myPID_AD.SetMode(AUTOMATIC);
//  myPID_AD.SetOutputLimits(-50, 210);
//  
//  myPID.SetSampleTime(1);
//  myPID_STEP.SetSampleTime(1);
//  
  moveStraight2(10);
  delay(500);
  moveStraight2(10);
  delay(500);
  moveStraight2(10);
  delay(500);  
  moveStraight2(10);
  delay(500);
  moveStraight2(10);
  delay(500); 
/////////////////////////////////////////////////

  //////////////////////////ROTATION TEST////////////////////////////////////////  
  //  motor.setSpeeds(-100,100);
  //  
  //  while (encoders.getCountsM1() > -900 || encoders.getCountsM2() <900)
  //  {    
  //    if (encoders.getCountsM2() >900)
  //    {
  //      motor.setM2Brake(400);
  //    }
  //    
  //    if (encoders.getCountsM1() <-900)
  //    {
  //      motor.setM1Brake(400);
  //    } 
  //    Serial.print("Left = ");
  //    Serial.print(encoders.getCountsM1());
  //    Serial.print("; Right = ");
  //    Serial.println(encoders.getCountsM2());
  //  }
  //  motor.setBrakes(400,400);
  /////////////////////////////////////////////////////////////////////////

  //    Serial.print("Left = ");
  //    Serial.print(encoders.getCountsM1());
  //    Serial.print("; Right = ");
  //    Serial.println(encoders.getCountsM2());

}

void loop(){
}

//////////////////////////ROTATION////////////////////////////////////////  
void turnLeft(int degree, int power)
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();

  int tickGoal =(double) degree*930/53 + (degree/90)*10;

  motor.setSpeeds(-power, power-18);

  while (encoders.getCountsM1() > -tickGoal || encoders.getCountsM2() < tickGoal)
  {
    if (encoders.getCountsM1() <-tickGoal/2)
      motor.setSpeeds(-power/2,power/2-9);

    if (encoders.getCountsM2() > tickGoal) 
      motor.setM2Brake(400);

    if (encoders.getCountsM1() < -tickGoal)
      motor.setM1Brake(400);
  }
  motor.setBrakes(400,400);
}

void turnRight(int degree, int power)
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();

  int tickGoal = (double) degree*930/53 + (degree/90)*10;

  motor.setSpeeds(power, -power+18);

  while (encoders.getCountsM2() > -tickGoal || encoders.getCountsM1() < tickGoal)
  {
    if (encoders.getCountsM1() > tickGoal/2)
      motor.setSpeeds(power/2, -power/2+9);

    if (encoders.getCountsM2() < -tickGoal) 
      motor.setM2Brake(400);

    if (encoders.getCountsM1() > tickGoal)
      motor.setM1Brake(400);
  }
  motor.setBrakes(400,400);
}
////////////////////////////////////////////////////////////////////////////////////////

////////////////////moveStraight////////////////
//void moveStraight(int dist, int masterPower)
//{
//  int tickGoal = (double)dist * (1500/14); //**********************need cali
//  int totalTicks = 0;
//  int slavePower = masterPower;
//  int error = 0;
//  int kp = 2;           //**********************need cali 
//  encoders.getCountsAndResetM1();
//  encoders.getCountsAndResetM2();
//
//  while (abs(totalTicks) < tickGoal)
//  {
//    motor.setSpeeds(masterPower, slavePower);
//    Serial.print(error);
//    error = encoders.getCountsM1() - (encoders.getCountsM2() + 10);
//    slavePower += (double) error / kp;
//    delay(100);
//    
//    totalTicks += encoders.getCountsM1();//AndResetM1();
//    
//    Serial.print(encoders.getCountsAndResetM1());
//    Serial.print(" ; ");
//    Serial.println(encoders.getCountsAndResetM2());
////    encoders.getCountsAndResetM2();
//  }
//  motor.setBrakes(400,400);
//}
/////////////////////////////////////////////////


////////////movSstraight w PID///////////////////////
void moveStraight2(int dist){
  int masterSpeed = 150;
  int slaveSpeed = 150;
  int tickGoal = (double)dist * (1500/14); //**********************need cali
  int totalTicks = 0;

  while (totalTicks < tickGoal)
  {
    Input = ( encoders.getCountsM1() - encoders.getCountsM2() ) /(double)11.93131;
    Setpoint = 0;
    myPID.SetTunings(consKp, consKi, consKd);
    myPID.Compute();
    
    Serial.print(Output);
    Serial.print(" ; ");
    slaveSpeed = (double)slaveSpeed + Output;
    motor.setSpeeds(masterSpeed, slaveSpeed);
    
    totalTicks += encoders.getCountsM1();//AndResetM1();
    Serial.print(encoders.getCountsAndResetM1());
    Serial.print(" ; ");
    Serial.println(encoders.getCountsAndResetM2());
//    encoders.getCountsAndResetM2();
  }
  motor.setBrakes(400,400);
}
///////////////////////////////////////////////////

void resetEncoderCount()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}

