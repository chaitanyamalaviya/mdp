#include <DualVNH5019MotorShield.h>
#include <PololuWheelEncoders.h>
#include <PID_v1.h>

//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts
PID myPID(&Input, &Output, &Setpoint, consKp, consKi, consKd, DIRECT);
double Setpoint, Input, Output;
double consKp=0.4, consKi=0.1, consKd=0.2;

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

void resetEncoderCount()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}
