#include <PololuWheelEncoders.h>
#include <DualVNH5019MotorShield.h>

DualVNH5019MotorShield motor;
PololuWheelEncoders encoders;

int M1Speed = 200;    //Left Motor
int M2Speed = 190;   //Right Motor

String command;

void setup(){
  Serial.begin(9600);
  motor.init();
  encoders.init(3,5,11,13);
}

void loop(){
}


void nextStep(String command){
  switch (command)
  {
    case ("LT"): //turn left 90 degrees
      {
        turnLeft(90);
        break;
      }
    
    case ("RT"): //turn right 90 degrees
      {
        turnRight(90);
        break;
      }
    
    case ("FT"): //move forward 1 blk
      {
        moveStraight(10);
        break;
      }
    
    case ("BT"): //turn 180 degrees
      {
        turnLeft(180);
        break;
      }
    
    case ("ST"): //stop
      {
        motor.setBrakes(400,400);
        break;
      }
    
    case ("SX"): //start
      {
        break;
      }
    
    default:
    {
      break;
    }
  }  
}

//=======================================================================================//
//======================================MOVE STRAIGHT====================================//
void moveStraight(int distanceCM)
{
  int motorPower = 200;
  int m1Power = motorPower;
  int m2Power = (m1Power);
  int gap = 4;
  int error = 0;
  int totalCount = 0;
  
  int aggkp = 3;  

  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  
  while(Serial.available() <= 0)
  {
    if(distanceCM <= totalCount/115)//118.5 default
    {
      break;
    }

    motor.setM1Speed(m1Power%401);
    motor.setM2Speed(m2Power%401);
    
   
    error = encoders.getCountsM1() - encoders.getCountsM2();
    totalCount = totalCount + encoders.getCountsM1();
    
//    Serial.print("Error Speed ");
//    Serial.print(abs(error));
    
    if(abs(error) > gap)
    {
      m2Power = m2Power + (error/aggkp);
    }
    else
    {
      m2Power = m2Power - (1);
    }
    
    //Reset the encoders. every loop so we have a fresh value to use to calculate the error.
//    Serial.print("  M1= ");
//    Serial.print(encoders.getCountsAndResetM1());
//    Serial.print("  M2= ");
//    Serial.println(encoders.getCountsAndResetM2());
    resetEncoderCount();
    delay(100);
  }
  motor.setBrakes(motorPower,motorPower);
}

void resetEncoderCount()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}
//=======================================================================================//
//=======================================================================================//

//***************************************************************************************//
//***********************************TURN LEFT, RIGHT************************************//
void turnLeft(int degree)
{
  motor.setBrakes(400,400); 
  delay(50);
  int rot = (int)(degree*1.6);  //6.18
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    motor.setM1Speed(-M1Speed);
    motor.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
}

void turnRight(int degree)
{
  motor.setBrakes(400,400);  
  delay(50);
  int rot = (int)(degree*1.6);   //6.18
  for ( int i = 0; i <= rot ; i++)  //right
  {  
    motor.setM1Speed(M1Speed);
    motor.setM2Speed(-M2Speed);
    delay(5);
  }
  delay(100);
}
//***************************************************************************************//
//***************************************************************************************//

