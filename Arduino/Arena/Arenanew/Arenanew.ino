#include "DualVNH5019MotorShield.h"
#include <PololuWheelEncoders.h>

DualVNH5019MotorShield md;
PololuWheelEncoders encoders;

int M1Speed = 200;    //Left Motor
int M2Speed = 190;    //Right Motor
int sensorRead[6];
int urmreading;

char inData[50]; // Allocate some space for the string

char inChar; // Where to store the character read

void setup()
{
  boolean valexplore = false, valfp = false;
  encoders.init(3,5,11,13);
  Serial.begin(9600);
  Serial.println("PH: Dual VNH5019 Motor Shield");
  md.init();  
  PWM_Mode_Setup();
  valexplore = explore();
  if (valexplore)
    {  valfp = fastestPath();                        //back2start
       if (valfp)                                
            Serial.println("PH:Back at Start");     
    }            
    
  val2 = false;  
  val2 = fastestPath();                            //start2goal
  if (val2)
      Serial.println("Mission complete!");
}


boolean explore(){
  
  inData[0] = '\0';
  inData[3] = '\0';
  
  //char newch[] = {'P',':','L','T'};  
  Serial.println("PH:Exploration");
  int index = 0;
  while (Serial.available() > 0){
         
         if (index<4)    { 
           inChar = Serial.read(); // Read a character
           inData[index] = inChar; // Store it
           Serial.println("While reading");
           Serial.println(inData[index]);
           index++; // Increment where to write next
           inData[index] = '\0'; // Null terminate the string     
            }
   } 
   
  Serial.println("PH:Read complete with indata = ");
  Serial.println(inData);
 
   if (inData[3]=='X')
   { Serial.println("SX polls");
     sendSensordata();
   }
   else if (inData[0]!='\0')   
    { 
   
       if (explorenextMove(inData))
             sendSensordata();        
       else
         { Serial.println("Exploration complete");
           Serial.flush();
           return true;   
           Serial.println("Returned already");
         }           //Back2start fastest path
    }
  delay(200);
  explore();

//  Serial.flush();

}


boolean explorenextMove(char arg[])
{ 
 
  Serial.print(arg[2]);  
  switch(arg[2])
  {  
    
    case 'F': moveStraight(10);
              return true;
              break;
    case 'L': turnLeft();
              return true;
              break;
    case 'R': turnRight();
              return true;
              break;
    case 'B': turnAround();
              return true;
              break;          
    case 'S': moveStop();
              return false;
              break;
    default  : Serial.println("PH:Invalid command");    
              return true;    
  };

}


boolean fastestPath()
{ 
  Serial.println("This is fastest path");
  String str2;
  char inData2[50];
  
  inData2[0] = '\0';
  inData2[2] = '\0';
  int index = 0, i=0; 
   
    while (Serial.available()>0){
   
      if (index<50) {
           inChar = Serial.read(); // Read a character
           inData2[index] = inChar; // Store it
           Serial.println("While reading");
           Serial.println(inData2[index]);
           index++; // Increment where to write next
           inData2[index] = '\0'; // Null terminate the string                   
         }
//  String temp = String(inData);
//  str2 = temp.substring(2,temp.length());
    }
  
  i =2;

  Serial.println(inData2);
//  Serial.println(str2);
  
  while (inData2[i]!='\0'){ 

    Serial.println(inData2[i]);
    
    switch(inData2[i])
    {  
    case 'A': Serial.println("PH:Arduino Start!");
              break;  
    case 'F': moveStraight(10);
              break;
    case 'L': turnLeft(); 
              break;
    case 'R': turnRight();
              break;
    case 'B': turnAround();
              break;          
    case 'S': moveStop();
              return true;
              break;
    default : Serial.println("PH:Invalid command");
    };
  
    i = i+2;
  
   }
   
   delay(500);
  fastestPath();
}

void loop(){
 
  
}


//void StartToGoal(){
//
//  String str2 = arg.substring(2,arg.length());
//  
//  switch(str2[i])
//  {  
//    case 'A': sendSensordata(); 
//              break;
//    case 'F': moveStraight(10);
//              break;
//    case 'L': turnLeft(); 
//              break;
//    case 'R': turnRight();
//              break;
//    case 'B': turnAround();
//              break;          
//    case 'S': moveStop()
//              break;
//  };
//  
//  i = i+2;
//
//}
//






//void detectObstacle()
//{
//  
//  if (sensorRead[0]>540 || sensorRead[2]>550 || sensorRead[4]>400)  //Obstacle detected in straight line
//    {md.setM1Speed(0);
//     md.setM2Speed(0);
//     delay(200);          
//     if (analogRead(1) > 400 )
//       { turnLeft();
//         moveStraight(10);
//         turnRight();
//         moveStraight(20);
//         turnRight();
//         moveStraight(10);
//         turnLeft(); 
//       }
//      else 
//       {  turnRight();  
//          moveStraight(25);
//          turnLeft();
//          moveStraight(40);
//          turnLeft();
//          moveStraight(20);
//          turnRight(); 
//       }
//       counter=counter+4;
//     }  
//} 

void stopIfFault()
{
  if (md.getM1Fault())
  {
    Serial.println("M1 fault");
    while(1);
  }
  if (md.getM2Fault())
  {
    Serial.println("M2 fault");
    while(1);
  }
}

void turnLeft()
{
  moveStop();  
  int rot = (int)(90*1.63);  //6.18
  Serial.println("Turning left");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
  moveStop();
}


void turnRight()
{
  
  int rot = (int)(90*1.63);   //6.18
  Serial.println("Turning right");
  for ( int i = 0; i <= rot ; i++)  //right
  {  
    md.setM1Speed(M1Speed);
    md.setM2Speed(-M2Speed);
    delay(5);
  }
  delay(100);
  moveStop();
}

void turnAround()
{
  moveStop();  
  int rot = (int)(180*1.63);  //6.18
  Serial.println("Turning 180*");
  for ( int i = 0; i <= rot ; i++)  //left
  {  
    md.setM1Speed(-M1Speed);
    md.setM2Speed(M2Speed);
    delay(5);
  } 
  delay(100);
   moveStop();
}

void moveStop(){
    md.setBrakes(400,400);
    delay(1000);
}


