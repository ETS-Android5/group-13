# Group-13
### Development Team
- [Jonathan Bergdahl](https://github.com/jonathanb00)
- [Juan García Díaz](https://github.com/JuanDoesCoding)
- [Kristofer Koskunen](https://github.com/K0ssu)
- [Mijin Kim](https://github.com/mezyn)
- [Adam Ekwall](https://github.com/AdamEkwall)
- [William Hilmersson](https://github.com/bobman97)

### What is SnailCar?
Our goal as a team was to produce a software prototype capable of obstacle and cliff avoidance for a manual control car, also described as "Assisted driving". In order to test and develop the prototype we have made we have created a realistic map setting for the SMCE emulator that features elements that will allow the user to test all the assisted driving features. The car is controlled by a joystick from an android application which features an intuitive and user-friendly design. 

### Why did we make it?
A safer driving environment is something we think everyone wants to achieve and according to our development team assisted driving is a step forward in achieving this. When developing this product we aimed to showcase the benefits of assisted driving features to raise awareness of the possibilities that exist in terms of improving safety in traffic.


### What problem does it solve?
It is our belief that human error is the biggest causation of accidents on the road, where the majority of small accidents occur in a city environment. Our goal is to support the driver with sensors that are able to see things in real time and make inputs on the car according to the data which it acquires. Along with this we have implemented the ability to have the car turn its wheels in different directions to allow the car to turn in its current position which is useful for tight corners and common maneuvers such as pocket parking.

### How did we make it?
We adapted an agile working method by holding weekly team meetings where we split up the tasks amongst our members to be completed within a specified amount of time. In addition to this we have had weekly meetings with our TAs to receive feedback and suggestions on our development process.

The logic of the car was developed in Arduino with C++ and the android application was developed using java in Android studio. The android application communicates with the car in the emulator through a mosquitto broker where the app sends the input to the broker and the car emulator receives it. The maps have been developed in Godot and Autodesk maya. 

### Installation guide
Under construction

### User manual
[Click here to go to the user manual](https://github.com/DIT113-V22/group-13/wiki/User-Manual)

### Demo video
Under construction

### Software architecture
To showcase how the android application commuinicates with the SMCE emulator we have created the following sequence diagram:
![image](https://user-images.githubusercontent.com/90379630/170242207-ff3f8cec-a240-4c39-a198-9a2281c509ad.png)

The sequence diagram shows how the application behaves when the user starts the application. So firstly the user loads the sketch in SMCE Godot and starts the android application, then the android application and SMCE Godot attempts to connect to the Mosquitto broker until it receives a message which says that the connection is established. 

### What kind of technology have we used?
**Runtime environment:** SMCE-Godot, Pixel 2 API 29 (Android 10).

**Map development:** Autodesk Maya and Godot.

**Car logic development language:** Arduino and C++.

**GUI development language:** Android Studio + Java.
