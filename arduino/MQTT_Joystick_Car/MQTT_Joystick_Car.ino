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


//instantiation of car components
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

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

SimpleCar car(control);

//function to choose the broker URL
#ifdef __SMCE__
const auto mqttBrokerUrl = "broker.hivemq.com";
#else
const auto mqttBrokerUrl = "broker.hivemq.com";
#endif
const auto maxDistance = 400;


std::vector<char> frameBuffer;

//method to set up the car, MQTT client, wifi connection and MQTT message handling
void setup() {
  Serial.begin(9600);
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
  //handle message
  mqtt.onMessage([](String topic, String message) {
    if (topic == "DIT133Group13/LeftSpeed") {
      //Serial.println("changing left speed");
      leftMotor.setSpeed(message.toInt());
    } else if (topic == "DIT133Group13/RightSpeed") {
      //Serial.println("changing right speed");
      rightMotor.setSpeed(message.toInt());
    }
  });
}

//loop that reads new input and maintains the connection to the MQTT client
void loop() {
   if (mqtt.connected()) {
    mqtt.loop();
   }
   if (!mqtt.connected()){
      mqtt.connect("arduino", "public", "public");
      mqtt.subscribe("DIT133Group13/#", 1);
   }
  if (frontIR.getDistance() > 0){
    Serial.print("Front: ");
    Serial.println(frontIR.getDistance());
   }
   if (backIR.getDistance() > 0) {
    Serial.print("Back: ");
    Serial.println(backIR.getDistance());
   }
   if (leftIR.getDistance() > 0){
    Serial.print("Left: ");
    Serial.println(leftIR.getDistance());
   }
   if (rightIR.getDistance() > 0){
    Serial.print("Right: ");
    Serial.println(rightIR.getDistance());
   }

// Avoid over-using the CPU if we are running in the emulator
#ifdef __SMCE__
  delay(35);
#endif
         
}
