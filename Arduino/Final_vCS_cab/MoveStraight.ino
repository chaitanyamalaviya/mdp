//wheel radius = 3cm
//wheel circumference = 18.84955cm = 188.5mm
//encoder count per rotation = 2249
//encoder count per mm = 2249 / 188.5mm = 12 counts

//void moveStraight(int dist)
//{
//  int tickGoal =(double) dist * 95;//(double)dist * (1110/7.8);
//
//  int motorPower = 200;
//  int m1Power = motorPower;
//  int m2Power = (m1Power);
//
//  int gap = 8;
//  double aggkp = 6.5;
//
//  encoders.getCountsAndResetM1();
//  encoders.getCountsAndResetM2();
//
//  while(encoders.getCountsM1() < tickGoal)
//  {
//    error = encoders.getCountsM1() - encoders.getCountsM2() + preError;
//    preError = 0;
//
//    if(error > gap)
//      m2Power = m2Power + (error/aggkp);
//    else
//      m2Power = m2Power + (error/8);
//
//    if (encoders.getCountsM1() >= tickGoal )
//      md.setM1Brake(400);
//    else
//      md.setM1Speed(m1Power%401);
//
//    if (encoders.getCountsM2() >= tickGoal )
//      md.setM2Brake(400);
//    else
//      md.setM2Speed(m2Power%401);
//
//    //    Serial.print(encoders.getCountsM1());
//    //    Serial.print(" ; ");
//    //    Serial.println(encoders.getCountsM2());
//
//    //    delay(28);
//  }
//  md.setBrakes(motorPower,motorPower);
////  preError =  encoders.getCountsAndResetM1() - encoders.getCountsAndResetM2();
//  delay(100);
//
//  checkWallAndAlign();
//}

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
  //checkWallAndAlign();
  if (( PWM_Mode()< 14 && PWM_Mode() > 1 ) && frontLeft>330 && frontRight>330 ){
    checkWallAndAlignCS(); 
  }
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
  int counta = 5;
  int countb = 10;
  int countc = 10;

  updateIRRead();
  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0){/*countc=10;*/ break;}
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

    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) {countb = 16; break;}

      if ((frontLeft > initial_frontLeft-40) && (frontRight < initial_frontRight+40)){
        md.setSpeeds(-100,100);
        delay(25);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+40 ) && (frontRight > initial_frontRight-40)){
        md.setSpeeds(100,-100);
        delay(25);
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
  int countb = 16;
  int countc = 10;  
  
  turnRight();
  updateIRRead();

  while ((PWM_Mode() != 8) || abs(frontLeft - initial_frontLeft) > 50 || abs(frontRight - initial_frontRight) > 50){
    //calibrate the distance between robot and wall
    if (countc == 0){/*countc=10;*/ break;}
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

    //calibrate the angle, make the robot parallel with wall
    while (abs(frontLeft - initial_frontLeft) > offseta || abs(frontRight - initial_frontRight) > offseta){
      if (countb == 0) {countb = 16; break;}

      if ((frontLeft > initial_frontLeft-40) && (frontRight < initial_frontRight+40)){
        md.setSpeeds(-80,80);
        delay(20);
        updateIRRead(); 
      }
      else if ((frontLeft < initial_frontLeft+40 ) && (frontRight > initial_frontRight-40)){
        md.setSpeeds(80,-80);
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

void resetEncoderCount()
{
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}

void resetEncoders(){
  encoders.getCountsAndResetM1();
  encoders.getCountsAndResetM2();
}

void checkWallAndAlignCS(){
  int counterA =0;
  //calibrateangle
  while (getMedianPWM() != 8 && counterA < 20){
      int a = getMedianPWM()-8;
      if (a > 0){
        moveStraight2(1, 100);
      }
      else {
        moveStraight2(1,-100); //moveStraight(-a,-100);
      }
      counterA++;
  }
  
  if (analogRead(A0)>330 && analogRead(A3)>330){
    int counter = 0;
    while (abs(getMedianA0()-getMedianA3()) > 50 && counter < 200){
      if(getMedianA0()>getMedianA3()){
        md.setSpeeds(-100,100);
        delay(50);
      }
      else{
        md.setSpeeds(100,-100);
        delay(50);
      }
      md.setBrakes(400,400);
      counter++;
    }  
  }
}


int getMedianA0()
{
  int median = 0;
  int analogValues[10];
  int readingNumber;
  int numReadings = 10;
  
  for (readingNumber = 0; readingNumber < numReadings; readingNumber++) {
    //get the reading:
    analogValues[readingNumber] = analogRead(A0);
    // increment the counter:
    readingNumber++;
  }
  
  
  int out, in, swapper;
  for(out=0 ; out < numReadings; out++) {  // outer loop
    for(in=out; in<(numReadings-1); in++)  {  // inner loop
      if( analogValues[in] > analogValues[in+1] ) {   // out of order?
        // swap them:
        swapper = analogValues[in];
        analogValues [in] = analogValues[in+1];
        analogValues[in+1] = swapper;
      }
    }
  }
  
  return median = analogValues[numReadings / 2]; 
  
}

int getMedianA3()
{
  int median = 0;
  int analogValues[10];
  int readingNumber;
  int numReadings = 10;
  
  for (readingNumber = 0; readingNumber < numReadings; readingNumber++) {
    //get the reading:
    analogValues[readingNumber] = analogRead(A3);
    // increment the counter:
    readingNumber++;
  }
  
  
  int out, in, swapper;
  for(out=0 ; out < numReadings; out++) {  // outer loop
    for(in=out; in<(numReadings-1); in++)  {  // inner loop
      if( analogValues[in] > analogValues[in+1] ) {   // out of order?
        // swap them:
        swapper = analogValues[in];
        analogValues [in] = analogValues[in+1];
        analogValues[in+1] = swapper;
      }
    }
  }
  
  return median = analogValues[numReadings / 2]; 
  
}

int getMedianPWM()
{
  int median = 0;
  int analogValues[10];
  int readingNumber;
  int numReadings = 10;
  
  for (readingNumber = 0; readingNumber < numReadings; readingNumber++) {
    //get the reading:
    analogValues[readingNumber] = PWM_Mode();
    // increment the counter:
    readingNumber++;
  }
  
  
  int out, in, swapper;
  for(out=0 ; out < numReadings; out++) {  // outer loop
    for(in=out; in<(numReadings-1); in++)  {  // inner loop
      if( analogValues[in] > analogValues[in+1] ) {   // out of order?
        // swap them:
        swapper = analogValues[in];
        analogValues [in] = analogValues[in+1];
        analogValues[in+1] = swapper;
      }
    }
  }
  
  return median = analogValues[numReadings / 2]; 
  
}

