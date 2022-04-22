


//#include <Vector.h>
#include <vector>

#include <MQTT.h>
#include <WiFi.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif
#include <Smartcar.h>

MQTTClient mqtt;
WiFiClient net;

const char ssid[] = "...";
const char pass[] = "....";

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime,
                       smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime,
                        smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
const int fSpeed = 70;
const int bSpeed = -70;
const int lTurn = -10;
const int rTurn = 10;
const int lRotate = -30;
const int rRotate = 30;
SimpleCar car(control);

const auto oneSecond = 1000UL;

const auto triggerPin = 6;
const auto echoPin = 7;
const auto mqttBrokerUrl = "127.0.0.1";

const auto maxDistance = 400;
SR04 front(arduinoRuntime, triggerPin, echoPin, maxDistance);

std::vector<char> frameBuffer

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
#ifdef __SMCE__
  Camera.begin(QVGA, RGB888, 15);
  frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
#endif

  WiFi.begin(ssid, pass);
  mqtt.begin(mqttBrokerUrl, 1883, net);

  Serial.println("Connecting to WiFi...");
  auto wifiStatus = WiFi.status();
  while (wifiStatus != WL_CONNECTED && wifiStatus != WL_NO_SHIELD) {
    Serial.println(wifiStatus);
    Serial.print(".");
    delay(1000);
    wifiStatus = WiFi.status();
  }


  Serial.println("Connecting to MQTT broker");
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }

  mqtt.subscribe("/smartcar/control/#", 1);
  mqtt.onMessage([](String topic, String message) {
    if (topic == "/smartcar/control/throttle") {
      car.setSpeed(message.toInt());
    } else if (topic == "/smartcar/control/steering") {
      car.setAngle(message.toInt());
    } else {
      Serial.println(topic + " " + message);
    }
  });
}


void loop() {
  // put your main code here, to run repeatedly:
  handleInput();
  if (mqtt.connected()) {
    mqtt.loop();
    const auto currentTime = millis();
#ifdef __SMCE__
    static auto previousFrame = 0UL;
    if (currentTime - previousFrame >= 65) {
      previousFrame = currentTime;
      Camera.readFrame(frameBuffer.data());
      mqtt.publish("/smartcar/camera", frameBuffer.data(), frameBuffer.size(),
                   false, 0);
    }
#endif
    static auto previousTransmission = 0UL;
    if (currentTime - previousTransmission >= oneSecond) {
      previousTransmission = currentTime;
      const auto distance = String(front.getDistance());
      mqtt.publish("/smartcar/ultrasound/front", distance);
    }
  }
#ifdef __SMCE__
  // Avoid over-using the CPU if we are running in the emulator
  delay(1);
#endif

}

void handleInput() {
  if (Serial.available()) {
    char input = Serial.read(); // read everything that has been received so far and log down
    // the last entry
    switch (input) {
      case 'w': // Car moves straight forward
        car.setSpeed(fSpeed);
        car.setAngle(0);
        break;
      case 's': // Car moves straight backwards
        car.setSpeed(bSpeed);
        car.setAngle(0);
        break;
      case 'a': // The car turns left while the car is in motion
        car.setAngle(lTurn);
        break;
      case 'd': // The car turns right while the car is in motion
        car.setAngle(rTurn);
        break;
      case 'q': //The car rotates to the left when standing still, if moving it comes to a stop
        car.setSpeed(0);
        car.setAngle(0);
        delay(1000);
        car.overrideMotorSpeed(lRotate, rRotate);
        break;
      case 'e': //The car rotates to the right when standing still, if moving it comes to a stop
        car.setSpeed(0);
        car.setAngle(0);
        delay(1000);
        car.overrideMotorSpeed(rRotate, lRotate);
        break;
      default: // Any other input stops the car
        car.setSpeed(0);
        car.setAngle(0);
    }
  }
}
