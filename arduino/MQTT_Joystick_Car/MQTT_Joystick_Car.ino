#include <vector>
#include <MQTT.h>
#include <WiFi.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif
#include <Smartcar.h>

MQTTClient mqtt;
WiFiClient net;

//wifi credentials
const char ssid[] = "***";
const char pass[] = "****";

// INFRARED SENSORS
const auto FRONT_IR_PIN = 0;
const auto LEFT_IR_PIN = 1;
const auto RIGHT_IR_PIN = 2;
const auto BACK_IR_PIN = 3;
const auto FRONT_INNER_LEFT_IR_PIN = 40;
const auto FRONT_OUTER_LEFT_IR_PIN = 41;
const auto FRONT_INNER_RIGHT_IR_PIN = 42;
const auto FRONT_OUTER_RIGHT_IR_PIN = 43;
const auto BACK_INNER_LEFT_IR_PIN = 50;
const auto BACK_OUTER_LEFT_IR_PIN = 51;
const auto BACK_INNER_RIGHT_IR_PIN = 52;
const auto BACK_OUTER_RIGHT_IR_PIN = 53;

// Topic keywords
const String leftMotorSpeed = "DIT133Group13/LeftSpeed";
const String rightMotorSpeed = "DIT133Group13/RightSpeed";
const String keepSpeed = "DIT133Group13/CruiseControl";
const String rotateLeft = "DIT133Group13/RotateLeft";
const String rotateRight = "DIT133Group13/RotateRight";


//instantiation of car components
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
GY50 gyroscope(arduinoRuntime, 37);
const auto pulsesPerMeter = 600;
DirectionlessOdometer leftOdometer(arduinoRuntime, smartcarlib::pins::v2::leftOdometerPin,[]() { leftOdometer.update(); }, pulsesPerMeter);
DirectionlessOdometer rightOdometer(arduinoRuntime, smartcarlib::pins::v2::rightOdometerPin,[]() { rightOdometer.update(); }, pulsesPerMeter);


//instantiates infrared sensors
GP2D120 frontIR(arduinoRuntime, FRONT_IR_PIN); // measure distances between 5 and 25 centimeters
GP2D120 backIR(arduinoRuntime, BACK_IR_PIN);
GP2D120 leftIR(arduinoRuntime, LEFT_IR_PIN);
GP2D120 rightIR(arduinoRuntime, RIGHT_IR_PIN);
GP2D120 fronInnerLeftIR(arduinoRuntime, FRONT_INNER_LEFT_IR_PIN);
GP2D120 frontOuterLeftIR(arduinoRuntime, FRONT_OUTER_LEFT_IR_PIN);
GP2D120 frontInnerRightIR(arduinoRuntime, FRONT_INNER_RIGHT_IR_PIN);
GP2D120 frontOuterRightIR(arduinoRuntime, FRONT_OUTER_RIGHT_IR_PIN);
GP2D120 backInnerLeftIR(arduinoRuntime, BACK_INNER_LEFT_IR_PIN);
GP2D120 backOuterLeftIR(arduinoRuntime, BACK_OUTER_LEFT_IR_PIN);
GP2D120 backInnerRightIR(arduinoRuntime, BACK_INNER_RIGHT_IR_PIN);
GP2D120 backOuterRightIR(arduinoRuntime, BACK_OUTER_RIGHT_IR_PIN);

// Create car object and apply all modules.
SmartCar car(arduinoRuntime, control, gyroscope, leftOdometer, rightOdometer);

// Global Car-Control variables
boolean blockForward = false;
boolean blockReverse = false;
boolean cruiseControl = false;
boolean rotateCar = false;
int carSpeed = 100;
int lmSpeed = 0;
int rmSpeed = 0;


//Define MQTT Broker
#ifdef __SMCE__
const auto mqttBrokerUrl = "broker.hivemq.com";
#else
const auto mqttBrokerUrl = "broker.emqx.io";
#endif
const auto maxDistance = 400;

// For camera?
std::vector<char> frameBuffer;



void loop() {

  // put your main code here, to run repeatedly:
  if (mqtt.connected()) {
    mqtt.loop();
  }

  if (!mqtt.connected()) {
    mqtt.connect("arduino", "public", "public");
    mqtt.subscribe("DIT133Group13/#", 1);
  }
  // Verify cars surroundings
  checkSensors();

  // Keeps car in requested input.
  maintainSpeed();

  // Avoid over-using the CPU if we are running in the emulator
#ifdef __SMCE__
  delay(35);
#endif
}

/**
 * Checks the sensors if it's possible to move the vehicle back or forward. Prevents movement if car is blocked.
 */

void checkSensors() {
  int minDistance = 0;
  int maxDistance = 40;
  int carSpeed = car.getSpeed() * 3.6;
  int frontSensor = frontIR.getDistance();
  int backSensor = backIR.getDistance();
  
  // Checks if direction
  blockForward    = (frontSensor > minDistance && frontSensor < maxDistance);
  blockReverse    = (backSensor > 0 && backSensor < 40);

  
  if (carSpeed > 0 && blockForward) {         // If car is moving forward && path is blocked
    car.setSpeed(0);
  } else if (carSpeed < 0 && blockReverse) {  // if car is moving backwards and path is blocked
    car.setSpeed(0);
  }
}

/**
   Set the speed of left/right motor of car.
   Setting different speed will turn car and move it forward/backward
   @PARAM motor - Information about which motor's speed we're changing
   @PARAM newSpeed - The speed that will be set on the motor.
*/
void setSpeed(String motor, int newSpeed) {
  lmSpeed = rmSpeed = 0;
  rotateCar = false;
  // changing left motor speed and direction of movement is NOT blocked by sensors:
  if (motor == leftMotorSpeed && ((newSpeed > 0 && !blockForward) || (newSpeed < 0 && !blockReverse))) {
    leftMotor.setSpeed(newSpeed);
    // changing right motor speed and direction of movement is NOT blocked by sensors:
  } else if (motor == rightMotorSpeed && ((newSpeed > 0 && !blockForward) || (newSpeed < 0 && !blockReverse))) {
    rightMotor.setSpeed(newSpeed);
  } else {
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0);
  }
}

/**
   Still standing rotation. Checks if car is (almost) standing still before rotating.
*/

void stillStandingRotation(String direction, int toggle) {
  rotateCar = (toggle == 1 && car.getSpeed() < 0.2 && car.getSpeed() > -0.2 ? true : false);
  if (rotateCar && direction == rotateLeft) {
    lmSpeed = -50;
    rmSpeed = 50;
  } else if(rotateCar && direction == rotateRight) {
    lmSpeed = 50;
    rmSpeed = -50;
  } else { // Stops rotation.
    lmSpeed = 0;
    rmSpeed = 0;
    car.overrideMotorSpeed(lmSpeed, rmSpeed);
  }
}

/**
   Turns cruisecontrol on or off.
*/
void toggleCruiseControl() {
  cruiseControl = !cruiseControl;
}

/**
   This keeps rotations going as long as required without multi-threading
   It will also contain code for maintinging speed when cruisecontorl is activated.
*/

void maintainSpeed() {
  if(rotateCar) {
    car.overrideMotorSpeed(lmSpeed, rmSpeed);
  } else if(cruiseControl) {
    // Code here
  }
}

//method to set up the car, MQTT client, wifi connection and MQTT message handling
void setup() {
  Serial.begin(9600);
  blockForward = blockReverse = false;
#ifdef __SMCE__
  Camera.begin(QVGA, RGB888, 15);
  frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
#endif

  WiFi.begin(ssid, pass);
  mqtt.begin(mqttBrokerUrl, 1883, net);

  //WIFI connection while loop
  Serial.println("Connecting to WiFi...");
  auto wifiStatus = WiFi.status();
  while (wifiStatus != WL_CONNECTED && wifiStatus != WL_NO_SHIELD) {
    Serial.println(wifiStatus);
    Serial.print(".");
    delay(1000);
    wifiStatus = WiFi.status();
  }

  //MQTT Broker connection while loop
  Serial.println("Connecting to MQTT broker");
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }
  Serial.print("wifi status: ");
  //print wifi status in the serial, number 3 indicates a succesful connection
  Serial.print(wifiStatus);
  Serial.println(" ");
  mqtt.subscribe("DIT133Group13/#", 1);


  /**
     Receives and interprets message from MQTT Broker
     @PARAM topic - Is the attribute we are changing.
     @PARAM message - Is the new value we're changing it to.
  */

  mqtt.onMessage([](String topic, String message) {

    // If the topic sent is regarding motor-speed
    if ((topic == leftMotorSpeed || topic == rightMotorSpeed)) {
      setSpeed(topic, message.toInt());
    } else if (topic == keepSpeed) { // If message is togggling cruisecontrol
      toggleCruiseControl();
    } else if (topic == rotateLeft || topic == rotateRight) { // If message is toggling rotations
      stillStandingRotation(topic, message.toInt());
    }
  });

  // Keep emulator from overloading.
  delay(1);
}