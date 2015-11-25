//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

void moveStraight(int dist)
{
  int tickGoal =(double) dist * 94;//(double)dist * (1110/7.8);
  int totalTicks = 0;
  
  int motorPower = 200;
  int m1Power = motorPower;
  int m2Power = (m1Power);
  
  int gap = 2;
  int error = 0;
  
  //'Constant of proportionality' which the error is divided by. Usually this is a number between 1 and 0 the
  //error is multiplied by, but we cannot use floating point numbers. Basically, it lets us choose how much
  //the difference in encoder values effects the final power change to the motor.
 
  //best is 5 onward (4,13)= Slant abit (5,13) = slant right abit ) (5,14)= slant left, (4,14)= slant left
  double aggkp = 4;
  
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();

  while(encoders.getCountsM1() < tickGoal)
  {
    md.setM1Speed(m1Power%401);
    md.setM2Speed(m2Power%401);
   
    error = encoders.getCountsM1() - encoders.getCountsM2();
    //totalTicks += encoders.getCountsM1();
    
    if(error > gap)
    {
      m2Power = m2Power + (error*1.25/aggkp);
    }
    else
    {
      //m2Power = m2Power - (error/conskp);
      m2Power = m2Power - (1);
    }
//    Serial.print(encoders.getCountsM1());
//    Serial.print(" ; ");
//    Serial.println(encoders.getCountsM2());
    
    delay(28);
  }
  md.setBrakes(motorPower,motorPower);
  delay(100);
//  if (obs2 == false){
//    preright = analogRead(A4);
//    obs2 = true;
//  }else if (obs2 == true){
//    right = analogRead(A4);
//    obs2 = false;
//  }
//  if(abs(right - preright)<100 && abs(right - preright)>30){
//    preright = right;
//    obs2= true;
//    turnRight();
//    delay(250);
//    updateIRRead();
//    int dist_PWM = PWM_Mode();
//    // three blocks in front, calibrate distance and angle
//    if (( dist_PWM< 14) && frontLeft>330 && frontRight>330 ){
//     wallCali(); 
//    }
//    delay(500);
//    turnLeft();
//  }
//  if (count3>3 && analogRead(A4)>400){
//    turnRight();
//    delay(250);
//    updateIRRead();
//    int dist_PWM = PWM_Mode();
//    // three blocks in front, calibrate distance and angle
//    if (( dist_PWM< 14) && frontLeft>330 && frontRight>330 ){
//     wallCali(); 
//    }
//    delay(500);
//    turnLeft();
//    count3 = 0;
//  }
//  checkWallAndAlign();
//  count3++;
}

void checkWallAndAlign(){
  updateIRRead();
  int dist_PWM = PWM_Mode();
  // three blocks in front, calibrate distance and angle
  if (( dist_PWM< 14) && frontLeft>330 && frontRight>330 ){
     wallCali(); 
  }
  //only one block in front, calibrate distance
  else if (dist_PWM > 1 && dist_PWM < 14 && dist_PWM != 8){
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
  int countb = 10;
  int countc = 10;
  
  updateIRRead();
  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0)break;
    while (PWM_Mode() != 8){
      int a = PWM_Mode()-8;
      if (counta == 0) break;
      if (a > 0){
        moveStraight2(1, 100);
      }
      else {
        moveStraight2(1,-100); //moveStraight(-a,-100);
      }
      counta--;
    }
    
    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) break;
      
      if ((frontLeft > initial_frontLeft-40) && (frontRight < initial_frontRight+40)){
        md.setSpeeds(-100,100);
        delay(50);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+40 ) && (frontRight > initial_frontRight-40)){
        md.setSpeeds(100,-100);
        delay(50);
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
  int tickGoal = (double)dist * 85; //**********************need cali
  int totalTicks = 0;
  int slavePower = masterPower;
  if (masterPower < 0) slavePower += 15;
  else slavePower -= 15;
  int error = 0;
  int kp = 3;           //**********************need cali 
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
