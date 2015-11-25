void moveStraight(int dist){
  dist = 1125;
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
  delay(100);
  checkWallAndAlign();
}

//*************************************************************************** checkWallAndAlign(),wallCali(),blockCali()
//***************************************************************************
void checkWallAndAlign(){
  updateIRRead();
  int dist_PWM = PWM_Mode();
  // three blocks in front, calibrate distance and angle
  if (( dist_PWM< 14 && dist_PWM > 1 ) && frontLeft>330 && frontRight>330 ){
    wallCali(); 
  }
  //only one block in front, calibrate distance
//  else if (dist_PWM > 1 && dist_PWM < 14 && dist_PWM != 8){
//    blockCali();
//  }  
}

//void blockCali(){
//  int counta = 5;
//  while (PWM_Mode() !=8){
//    double a = PWM_Mode()-8;
//    if (counta == 0) break;
//    if (a > 0){
//      moveStraight2(1,100);
//    }
//    else {
//      moveStraight2(1,-100); 
//    }
//    counta--;
//  }
//}

void wallCali(){
  int counta = 8;
  int countb = 20 ;
  int countc = 10;

  updateIRRead();
  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0){/*countc=10;*/ break;}
    while (PWM_Mode() != 8){
      if (counta == 0) {/*counta = 10*/; break;}
      
      int a = PWM_Mode()-8;
      
      if (a > 0){
        moveStraight2(1, 100);
      }
      else {
        moveStraight2(2,-100); //moveStraight(-a,-100);
      }
      delay(5);
      counta--;
    }

    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) {countb = 16; break;}

      if ((frontLeft > initial_frontLeft-40) && (frontRight < initial_frontRight+40)){
        md.setSpeeds(-100,100);
        delay(20);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+40 ) && (frontRight > initial_frontRight-40)){
        md.setSpeeds(100,-100);
        delay(20);
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

//*************************************************************************** wallAlign()
//***************************************************************************
void wallAlign(){
  int counta = 16;
  int countb = 18;
  int countc = 10;  
  
  turnRight();
    updateIRRead();
  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0){/*countc=10;*/ break;}
    while (PWM_Mode() != 8){
      if (counta == 0) {/*counta = 10*/; break;}
      
      int a = PWM_Mode()-8;
      
      if (a > 0){
        moveStraight2(1, 100);
      }
      else {
        moveStraight2(2,-100); //moveStraight(-a,-100);
      }
      delay(5);
      counta--;
    }

    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) {countb = 16; break;}

      if ((frontLeft > initial_frontLeft-40) && (frontRight < initial_frontRight+40)){
        md.setSpeeds(-100,100);
        delay(20);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+40 ) && (frontRight > initial_frontRight-40)){
        md.setSpeeds(100,-100);
        delay(20);
        updateIRRead(); 
      }
      md.setBrakes(400,400);
      updateIRRead();
      countb--;
    }
    updateIRRead(); 
    countc--;
  }
  turnLeft();

}

void moveStraight2(double dist, int masterPower)
{
  int tickGoal = (double)dist * 45; //**********************need cali
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

void resetEncoders(){
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}

