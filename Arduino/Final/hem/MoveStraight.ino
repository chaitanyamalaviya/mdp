//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void moveStraight(int dist)
{
  int tickGoal =(double) dist * 95;//(double)dist * (1110/7.8);
  int totalTicks = 0;
  
  int motorPower = 200;
  int m1Power = motorPower;
  int m2Power = (m1Power);
  
  int gap = 8;
  int error = 0;
  int totalCount = 0;
  //'Constant of proportionality' which the error is divided by. Usually this is a number between 1 and 0 the
  //error is multiplied by, but we cannot use floating point numbers. Basically, it lets us choose how much
  //the difference in encoder values effects the final power change to the motor.
 
  //best is 5 onward (4,13)= Slant abit (5,13) = slant right abit ) (5,14)= slant left, (4,14)= slant left
  double aggkp = 6.5;
  
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();

  while(encoders.getCountsM1() < tickGoal)
  {
    error = encoders.getCountsM1() - encoders.getCountsM2() + preError;
    preError = 0;

    if(error > gap)
      m2Power = m2Power + (error/aggkp);
    else
      m2Power = m2Power + (error/8);
      
    if (encoders.getCountsM1() >= tickGoal )
      md.setM1Brake(400);
    else
      md.setM1Speed(m1Power%401);
      
    if (encoders.getCountsM2() >= tickGoal )
      md.setM2Brake(400);
    else
      md.setM2Speed(m2Power%401);
//    Serial.print(encoders.getCountsM1());
//    Serial.print(" ; ");
//    Serial.println(encoders.getCountsM2());
    
//    delay(28);
  }
  md.setBrakes(motorPower,motorPower);
  delay(100);
  checkWallAndAlign();
}

void checkWallAndAlign(){
  updateIRRead();
  // three blocks in front, calibrate distance and angle
  if (( PWM_Mode()< 14) && frontLeft>330 && frontRight>330 ){
     wallCali(); 
  }
  //only one block in front, calibrate distance
  else if (PWM_Mode() < 14 && PWM_Mode() != 8){
      blockCali();
  }  
}

void blockCali(){
  int counta = 5;
    while (PWM_Mode() !=8){
      double a = PWM_Mode()-8;
      if (counta == 0) break;
      if (a > 0){
        moveStraight2(1,100);
      }
      else {
        moveStraight2(1,-100); 
      }
      counta--;
    }
}

void wallCali(){
  int counta = 5;
  int countb = 16;
  int countc = 10;
  
  updateIRRead();
  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0){countc=10; break;}
    while (PWM_Mode() != 8){
      int a = PWM_Mode()-8;
      if (counta == 0) {counta = 10; break;}
      if (a > 0){
        moveStraight2(1, 100);
      }
      else {
        moveStraight2(1,-100); //moveStraight(-a,-100);
      }
      counta--;
    }
    
    updateIRRead();
    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) {countb = 16; break;}
      
      if ((frontLeft > initial_frontLeft-50) && (frontRight < initial_frontRight+50)){
        md.setSpeeds(-100,100);
        delay(30);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+50 ) && (frontRight > initial_frontRight-50)){
        md.setSpeeds(100,-100);
        delay(30);
        updateIRRead(); 
      }
      md.setBrakes(400,400);
      updateIRRead();
      countb--;
    }
    updateIRRead(); 
    countc--;
  }
}

void moveStraight2(double dist, int masterPower)
{
  int tickGoal = (double)dist * 20; //**********************need cali
  int totalTicks = 0;
  int slavePower = masterPower;
  if (masterPower < 0) slavePower += 5;
  else slavePower -= 10;
  int error = 0;
  int kp = 5;           //**********************need cali 
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();

  while (abs(totalTicks) < tickGoal)
  {
    md.setSpeeds(masterPower, slavePower);
    error = encoders.getCountsM1() - encoders.getCountsM2();
    slavePower += error / kp;
    delay(100);
    totalTicks += encoders.getCountsAndResetM1();
    encoders.getCountsAndResetM2();
  }
  md.setBrakes(400,400);
}

void updateIRRead(){
  frontLeft = analogRead(A0);
  frontRight = analogRead(A3);
}

void resetEncoderCount()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}
