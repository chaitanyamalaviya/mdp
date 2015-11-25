#include <DualVNH5019MotorShield.h>
#include <PololuWheelEncoders.h>
#include <PID_v1.h>

PololuWheelEncoders encoders;
DualVNH5019MotorShield md;

//moveStraight()
double Setpoint, Input, Output;
double Setpoint_AD, Input_AD, Output_AD;
double aggKp=0.001, aggKi=3.5, aggKd=0.01;
double consKp=40, consKi=20, consKd=10  ;
PID myPID(&Input, &Output, &Setpoint, consKp, consKi, consKd, DIRECT);
int masterspeed = 200;
int slavespeed = 200;

//moveStraight2()
double SetpointR, SetpointL, OutputL, OutputR;
static  double leftCnt = 0;
static  double rightCnt = 0;
double rightPWM = 0;
double leftPWM = 0;
PID pid_R((double*)encoders.getCountsM2(), &rightPWM, &SetpointR ,2, 1, 1, DIRECT);
PID pid_L((double*)encoders.getCountsM1(), &leftPWM, &SetpointL ,2, 1, 1, DIRECT);

void setup(){
  md.init();
  encoders.init(3,5,11,13);
  Serial.begin(9600);
  
  //moveStraight()
  Setpoint = 0;
  myPID.SetMode(AUTOMATIC);
  myPID.SetSampleTime(8);
  myPID.SetOutputLimits(-1, 1);
   
  //moveStraight2()
  pid_R.SetSampleTime(10);
  pid_L.SetSampleTime(10);
  pid_L.SetMode(AUTOMATIC);  
  pid_R.SetMode(AUTOMATIC); 
}

void loop(){
  delay(1000);
  moveStraight(1070);
  //  moveStraight2(1070);
  delay(3000);
}

void moveStraight(int dist){
  dist = 1070;
  int tickGoal = dist;
  resetEncoders();
  
  while (encoders.getCountsM1() < tickGoal || encoders.getCountsM2()<tickGoal){
    Input = (encoders.getCountsM1() - encoders.getCountsM2())/(double)11;
    myPID.SetTunings(consKp, consKi, consKd);
    myPID.Compute();
    slavespeed = (double)slavespeed - Output;
    
    if (encoders.getCountsM1() >= tickGoal) md.setM1Brake(400);
    else md.setM1Speed(masterspeed);
    
    if (encoders.getCountsM2() >= tickGoal) md.setM2Brake(400);
    else md.setM2Speed(slavespeed);
    
    delay(5);
  }
  md.setBrakes(400,400);
}

void moveStraight2(int dist){
  int conditionRight=0;
  int conditionLeft=0;
  SetpointR = dist;
  SetpointL = dist;
  rightPWM=0;
  leftPWM=0;
  resetEncoders();
  
  while(1){
    rightCnt = encoders.getCountsM2();
    leftCnt = encoders.getCountsM1();
    
    if (conditionRight==1 && conditionLeft==1){
      md.setBrakes(400,400);
      break;
    }
    pid_L.Compute();
    pid_R.Compute(); 
    
    if(!conditionRight)   md.setM2Speed(rightPWM+45); 
    if(!conditionLeft)    md.setM1Speed(leftPWM);
    
    if(encoders.getCountsM2()>=dist) {
       conditionRight = 1; 
       md.setM2Brake(400);
     }  
     if(encoders.getCountsM1()>=dist)  {
       conditionLeft = 1;
       md.setM1Brake(400);
     }
  }
  resetEncoders();
}

void resetEncoders(){
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}

void turnLeft()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  
  int degree = 90;
  int power = 200;
  int tickGoal =(double) degree*930/53 + (degree/90)*10;

  md.setSpeeds(-power, power-18);

  while (encoders.getCountsM1() > -tickGoal || encoders.getCountsM2() < tickGoal)
  {
    if (encoders.getCountsM1() <-tickGoal/2)
      md.setSpeeds(-power/2,power/2-9);

    if (encoders.getCountsM2() > tickGoal) 
      md.setM2Brake(400);

    if (encoders.getCountsM1() < -tickGoal)
      md.setM1Brake(400);
  }
  md.setBrakes(400,400);
  delay(100);
  //checkWallAndAlign();
}


void turnRight()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  
  int degree = 90;
  int power = 200;
  int tickGoal = (double) degree*930/53 + (degree/90)*10;

  md.setSpeeds(power, -power+18);

  while (encoders.getCountsM2() > -tickGoal || encoders.getCountsM1() < tickGoal)
  {
    if (encoders.getCountsM1() > tickGoal/2)
      md.setSpeeds(power/2, -power/2+9);

    if (encoders.getCountsM2() < -tickGoal) 
      md.setM2Brake(400);

    if (encoders.getCountsM1() > tickGoal)
      md.setM1Brake(400);
  }
  md.setBrakes(400,400);
  delay(100);
  //checkWallAndAlign();
}
