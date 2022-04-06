#include <Smartcar.h>
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, 
smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, 
smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
const int fSpeed = 70;
const int bSpeed = -70;
 SimpleCar car(control);
void setup() {
  // put your setup code here, to run once:
   Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  handleInput();

}

void handleInput(){
  if (Serial.available()){
        char input = Serial.read(); // read everything that has been received so far and log down
                                    // the last entry
    switch (input){

        case 'w': // go ahead
            car.setSpeed(fSpeed);
            car.setAngle(0);
            break;

        case 's': //reverse car
            car.setSpeed(bSpeed);
            car.setAngle(0);
            break;

        default: // if you receive something that you don't know, just stop
            car.setSpeed(0);
            car.setAngle(0);
        }
  }
}
