# USER MANUAL

The following document includes instructions on how to make use of the software developed by Group 13. 


## [Files](https://github.com/DIT113-V22/group-13)
- **Car Control Folder**: Contains all Java files that make up the application to control the car, In order to make use of it, its necessary to have installed **Android Studio** and select the emulator phone **Pixel 2 API 29**, as this model is compatible with **Paho Eclipse**, the preferred library for the use of MQTT
- **Arduino Folder**: Contains two different sketches of car models to simulate the **Arduino ESP32** inside of **SMCE-GODOT**, a simulator for Arduino Sketches developed by Gothenburg University Students. The folder with the name **MQTT_Joystick_Car** includes the software developed by the members of group 13 as a proof of concept of different assisted driving features, the **SmartCar** folder contains a basic sketch of the car with Serial-Based controls used to test the environments developed by the team.
- **Modifications Folder**: Contains all files used for 3D modelling for the **MQTT_Joystick_Car** sketch and the map based on **[Inom Vallgraven](https://www.google.com/maps/place/Inom+Vallgraven,+Gotemburgo/@57.7039778,11.9514769,14z/data=!4m5!3m4!1s0x464ff368a2410f03:0x71b0c1e1cef80fe1!8m2!3d57.7022551!4d11.9615896)** in the form of a PCK file

## Requirements to run the application
- [Installation Guide](https://github.com/DIT113-V22/group-13/blob/master/Documentation/InstallationGuide.MD) should be completed
- Device should allow for the use of local host.
- Both the emulator and Car control application should connect to the same local host.

## [Application](https://github.com/DIT113-V22/group-13/wiki/Feature-2:-Graphical-user-interface)

Once and only once the steps in the [Installation Guide](https://github.com/DIT113-V22/group-13/blob/master/Documentation/InstallationGuide.MD) are completed, the user may run the application.
When opened, the application should look as the following Image:

![alt text](https://github.com/DIT113-V22/group-13/blob/master/Documentation/images/Application.png)



### By taking a quick look we can distinguish the following elements:
- Two buttons reading: **ROTATE LEFT** and **ROTATE RIGHT**
- Two buttons reading: **FIND LEFT PATH** and **FIND RIGHT PATH**
- One button reading **CRUISE CONTROL**
- One speedometer reading **0 KM/H**
- One **Joystick**
- One **FPS Counter** to display the frames per second
- One **UPS Counter** to display the updates per second


### ROTATE LEFT and ROTATE RIGHT
- These two buttons make one of the basic movement functionalities, while pressed, they send messages to the MQTT broker indicating that the car should rotate on spot to one side (determined by which button is pressed)

### FIND LEFT PATH and FIND RIGHT PATH
- These two buttons are meant to be used when a user encounters a frontal obstacle and wishes to find a new clear path either on the left or the right side of the car.
#### This functionality works as follows
- User gets stuck between obstacles
- User presses **Find Left Path**
- Car rotates left until no obstacles are detected
>**Note**: due to limitations on the range of the infrared sensors, this functionality only works when a frontal obstacle is detected within 40 centimetres, or 40 in game units

### SPEEDOMETER 
- As the name indicates, this textfield will provide the user with real time information on the speed of the car.

### JOYSTICK
- This joystick has an update rate of 30 times per second, and is responsible for the basic mobility of the car, when not pressed, the joystick will go back to its starting position and order the car to stop all movement.


 ## Testing enviroment
 To get a better understanding of the different uses that this software offers, we developed a testing environment based on the neighbourhood of Gothenburg called **[Inom Vallgraven](https://www.google.com/maps/place/Inom+Vallgraven,+Gotemburgo/@57.7039778,11.9514769,14z/data=!4m5!3m4!1s0x464ff368a2410f03:0x71b0c1e1cef80fe1!8m2!3d57.7022551!4d11.9615896)** . When installed using the [Installation Guide](https://github.com/DIT113-V22/group-13), the user can freely roam around the map, and test the assisted driving functionality in real life setting with moving obstacles
  ![alt text](https://github.com/DIT113-V22/group-13/blob/master/Documentation/images/Map.png)

## Sensors
In order to develop the assisted driving features, the **MQTT_Joystick_Car** includes **GP2D120 Infrared Sensors** that are distributed all around the car, with some of them pointing straight and the rest pointing to the surface around the vehicle. 

### The distribution is as follows:
- 5 straight sensors in the front
- 5 angled sensors on the front
- 5 straight sensors in the back
- 5 angled sensors on the back
- 2 sensors on the sides

## Assisted Driving
This functionality can be found in the [Arduino Sketch](https://github.com/DIT113-V22/group-13/blob/master/arduino/MQTT_Joystick_Car/MQTT_Joystick_Car.ino) and its purpose is to impede movement that would cause a collision with the car, or make it fall on a steep decline of terrain.

### [Obstacle avoidance](https://github.com/DIT113-V22/group-13/wiki/Feature-5:-Object-avoidance) works in the following way:
- User drives in the direction of an obstacle
- Sensors detect obstacle
- Software impedes the movement of the car in said direction
 >**optional step:** despite having the motors blocked, the car can still perform "in place" rotation, "FIND LEFT PATH" or "FIND RIGHT PATH"

### [Cliff avoidance](https://github.com/DIT113-V22/group-13/wiki/Feature-7:-Cliff-avoidance) works in the following way:
- User drives in the direction of a steep decline
- Angled sensors detect steep decline
- Software impedes the movement of the car in said direction
 >**optional step:** despite having the motors blocked, the car can still perform "in place" rotation, "FIND LEFT PATH" or "FIND RIGHT PATH"


