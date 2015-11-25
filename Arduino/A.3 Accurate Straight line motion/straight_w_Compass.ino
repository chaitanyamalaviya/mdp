#include <DualVNH5019MotorShield.h>
#include <Wire.h>
#include <HMC5883L.h>

HMC5883L compass;
int error = 1;
DualVNH5019MotorShield md;
int static first = 1;

void moveForward(int M1Speed, int M2Speed){  //forward
  md.setSpeeds(-M1Speed,M2Speed);
  delay(5);
}

void setup()
{
  // Initialize the serial port.
  Serial.begin(9600);

  Serial.println("Starting the I2C interface.");
  Wire.begin(); // Start the I2C interface.

  Serial.println("Constructing new HMC5883L");
  compass = HMC5883L(); // Construct a new HMC5883 compass.
    
  Serial.println("Setting scale to +/- 1.3 Ga");
  error = compass.SetScale(1.3); // Set the scale of the compass.
  if(error != 0) // If there is an error, print it out.
    Serial.println(compass.GetErrorText(error));
  
  Serial.println("Setting measurement mode to continous.");
  error = compass.SetMeasurementMode(Measurement_Continuous); // Set the measurement mode to Continuous
  if(error != 0) // If there is an error, print it out.
    Serial.println(compass.GetErrorText(error));
    
   md.init();
}

void loop(){
  float originalDegrees, headingDegrees;
  if (first){
    originalDegrees = headingDegrees = setheadingDegrees();
    first = 0;
    Serial.println("1.");
    Serial.println(originalDegrees);
  }
  
  headingDegrees = setheadingDegrees();
  
  if (headingDegrees > originalDegrees)
    moveForward(320,340);
  else if (headingDegrees < originalDegrees)
    moveForward(340,100);
  else
    moveForward(330,330);
}

float setheadingDegrees(){
  MagnetometerScaled scaled = compass.ReadScaledAxis();
  float heading = atan2(scaled.YAxis, scaled.XAxis);
  float declinationAngle = 0.0457;
  heading += declinationAngle;
  if(heading < 0)    heading += 2*PI;
  if(heading > 2*PI) heading -= 2*PI;
  float headingDegrees = heading * 180/M_PI;  
  Serial.println("2.");
  Serial.println(headingDegrees);  
  return headingDegrees;
}
