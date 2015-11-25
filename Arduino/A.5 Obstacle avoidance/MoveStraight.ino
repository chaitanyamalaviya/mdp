
//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts


void moveStraight(int distanceCM)
{
  int motorPower = 200;
  int m1Power = motorPower;
  int m2Power = (m1Power-2/*+3*/);
  int gap = 4;
  int error = 0;
  int totalCount = 0;
  //'Constant of proportionality' which the error is divided by. Usually this is a number between 1 and 0 the
  //error is multiplied by, but we cannot use floating point numbers. Basically, it lets us choose how much
  //the difference in encoder values effects the final power change to the motor.
 
  //best is 5 onward (4,13)= Slant abit (5,13) = slant right abit ) (5,14)= slant left, (4,14)= slant left
  int aggkp = 3;
//  int conskp = 5;
  
  //Reset the encoders..
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
  //Repeat ten times a second.
  while(Serial.available() <= 0)
  {
//    Serial.println("Total count: ");
//    Serial.println(totalCount);
//    Serial.println("Compared with");
//    Serial.println(distanceCM*119.31);
    if(distanceCM*118 <= totalCount)//118.5 default  , 119.31 default
      break;
 
    //Set the motor powers to their respective variables.
    md.setM1Speed(m1Power%401);
    md.setM2Speed(m2Power%401);
    
    
    //This is where the magic happens. The error value is set as a scaled value representing the amount the slave
    //motor power needs to change. For example, if the left motor is moving faster than the right, then this will come
    //out as a positive number, meaning the right motor has to speed up.
    
   
    error = encoders.getCountsM1() - encoders.getCountsM2();
    totalCount = totalCount + encoders.getCountsM1();
    
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
      m2Power = m2Power - (2);
    }
    
    //Reset the encoders. every loop so we have a fresh value to use to calculate the error.
    Serial.print("  M1= ");
    Serial.print(encoders.getCountsAndResetM1());
    Serial.print("  M2= ");
    Serial.println(encoders.getCountsAndResetM2());
    
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
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}
