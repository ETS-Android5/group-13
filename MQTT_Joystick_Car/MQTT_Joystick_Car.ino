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

//instanciation of car components
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

const auto oneSecond = 1000UL;
//function to choose pins and the broker URL
#ifdef __SMCE__
const auto triggerPin = 6;
const auto echoPin = 7;
const auto mqttBrokerUrl = "broker.hivemq.com";
#else
const auto triggerPin = 33;
const auto echoPin = 32;
const auto mqttBrokerUrl = "192.168.0.40";
#endif
const auto maxDistance = 400;
SR04 front(arduinoRuntime, triggerPin, echoPin, maxDistance);

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
      Serial.println("changing left speed");
      leftMotor.setSpeed(message.toInt());
    } else if (topic == "DIT133Group13/RightSpeed") {
      Serial.println("changing right speed");
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

// Avoid over-using the CPU if we are running in the emulator
#ifdef __SMCE__
  delay(35);
#endif
         
}
