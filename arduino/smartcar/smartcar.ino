#include <Smartcar.h>
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
