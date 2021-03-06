#include <PololuWheelEncoders.h>
#include <DualVNH5019MotorShield.h>

PololuWheelEncoders we;
DualVNH5019MotorShield md;


    

void setup()
{
  md.init();
  //we.init(11,13,3,5);
  we.init(3,5,11,13);
  Serial.begin(9600);
   
}

void loop()
{
  
//  if(Comp("1"))
//  {
//    moveStright(300);
//  }
//  else if(Comp("R"))
//  {
//    moveRight(90, 250);
//    delay(3000);  
//  }
//  else if(Comp("L"))
//  {
//    moveLeft(90, 250);
//    delay(3000);  
//  }
//  else if(Comp("S"))
//  {
//    md.setBrakes(400,400);  
//  }
//  else
//  {
//    md.setSpeeds(0,0);
//  }

  moveStright(100);
  
}

void moveStright(int distanceCM)
{
  int motorPower = 200;
  int m1Power = motorPower;
  int m2Power = (m1Power-12);
  int gap = 7;
  int error = 0;
  int totalCount = 0;
  //'Constant of proportionality' which the error is divided by. Usually this is a number between 1 and 0 the
  //error is multiplied by, but we cannot use floating point numbers. Basically, it lets us choose how much
  //the difference in encoder values effects the final power change to the motor.
 
  //best is 5 onward (4,13)= Slant abit (5,13) = slant right abit ) (5,14)= slant left, (4,14)= slant left
  int aggkp = 2;
//  int conskp = 5;
  
  //Reset the encoders.
  we.getCountsAndResetM1();
  we.getCountsAndResetM2();
  //Repeat ten times a second.
  while(Serial.available() <= 0)
  {
    if(distanceCM <= totalCount/115)//118.5 default
    {
      break;
    }
    //Set the motor powers to their respective variables.
    md.setM1Speed(m1Power%401);
    md.setM2Speed(m2Power%401);
    
    
    //This is where the magic happens. The error value is set as a scaled value representing the amount the slave
    //motor power needs to change. For example, if the left motor is moving faster than the right, then this will come
    //out as a positive number, meaning the right motor has to speed up.
    
   
    error = we.getCountsM1() - we.getCountsM2();
    totalCount = totalCount + we.getCountsM1();
    
    //determine the error after deducting from the 2 motor
    Serial.print("Error Speed ");
    Serial.print(abs(error));
    //m2Power = m2Power + (error/aggkp);
   
    if(abs(error) > gap)
    {
      //aggressive gain
      m2Power = m2Power + (error/aggkp);
    }
    else
    {
      //m2Power = m2Power - (error/conskp);
      m2Power = m2Power - (4/2);
    }
    
    //Reset the encoders every loop so we have a fresh value to use to calculate the error.
    Serial.print("  M1= ");
    Serial.print(we.getCountsAndResetM1());
    Serial.print("  M2= ");
    Serial.println(we.getCountsAndResetM2());
    
    //resetEncoderCount();
    
    //Makes the loop repeat ten times a second. If it repeats too much we lose accuracy due to the fact that we don't have
    //access to floating point math, however if it repeats to little the proportional algorithm will not be as effective.
    //Keep in mind that if this value is changed, kp must change accordingly.
    delay(100);
  }
  md.setBrakes(motorPower,motorPower);
  
}


void resetEncoderCount()
{
  we.getCountsAndResetM1();
  we.getCountsAndResetM2();
}
