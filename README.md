![](https://user-images.githubusercontent.com/90850693/170713249-c4d0fc9d-b91f-49a3-aba3-4c82a1bee473.png)

# Group-13
### Development Team
- [Jonathan Bergdahl](https://github.com/jonathanb00)
- [Juan García Díaz](https://github.com/JuanDoesCoding)
- [Kristofer Koskunen](https://github.com/K0ssu)
- [Mijin Kim](https://github.com/mezyn)
- [Adam Ekwall](https://github.com/AdamEkwall)
- [William Hilmersson](https://github.com/bobman97)

### What is SnailCar?
Our goal as a team was to produce a software prototype capable of obstacle and cliff avoidance for a manual control car, also described as "Assisted driving". In order to test and develop the prototype we have made we have created a realistic map setting for the SMCE emulator that features elements that will allow the user to test all the assisted driving features. The car is controlled by a joystick from an android application which features an intuitive and user-friendly design. Detailed information about all our features can be found on our [wiki page](https://github.com/DIT113-V22/group-13/wiki).

### Why did we make it?
A safer driving environment is something we think everyone wants to achieve and according to our development team assisted driving is a step forward in achieving this. When developing this product we aimed to showcase the benefits of assisted driving features to raise awareness of the possibilities that exist in terms of improving safety in traffic.


### What problem does it solve?
It is our belief that human error is the biggest causation of accidents on the road, where the majority of small accidents occur in a city environment. Our goal is to support the driver with sensors that are able to see things in real time and make inputs on the car according to the data which it acquires. Along with this we have implemented the ability to have the car turn its wheels in different directions to allow the car to turn in its current position which is useful for tight corners and common maneuvers such as pocket parking.

### How did we make it?
We adapted an agile working method by holding weekly team meetings where we split up the tasks amongst our members to be completed within a specified amount of time. In addition to this we have had weekly meetings with our TAs to receive feedback and suggestions on our development process.

The logic of the car was developed in Arduino with C++ and the android application was developed using java in Android studio. The android application communicates with the car in the emulator through a mosquitto broker where the app sends the input to the broker and the car emulator receives it. The maps have been developed in Godot and Autodesk maya. 

### Installation guide
[Click here to go to the Installation Guide](https://github.com/DIT113-V22/group-13/wiki/Installation-Guide)

### User manual
[Click here to go to the user manual](https://github.com/DIT113-V22/group-13/wiki/User-Manual)

### Demo video
[Click here to check our demo video](https://youtu.be/iNM-pwgYoPQ)

### Software and hardware architecture
To showcase how the android application commuinicates with the SMCE emulator we have created the following sequence diagram:
![image](https://user-images.githubusercontent.com/90379630/170242207-ff3f8cec-a240-4c39-a198-9a2281c509ad.png)

The sequence diagram shows how the application behaves when the user starts the application. So firstly the user loads the sketch in SMCE Godot and starts the android application, then the android application and SMCE Godot attempts to connect to the Mosquitto broker until it receives a message which says that the connection is established. 

To showcase how different components in our system commicates with each other we have created the following component diagram:
![](https://user-images.githubusercontent.com/90850693/170713677-c268aca8-e503-42ed-9cc2-2d095c0e2a40.png)

The component diagram shows how the software and hardware architecture communicate with each other. This is a low level showcasing of the structure of our project while the sequence diagram is a higher level showcasing of the structure and functionality. The purpose of our component diagram is so that anyone with UML knowledge can easily look at the diagram and get an understanding of how it is structured.

We have modified the hardware architecture by editing and adding more sensors to accommodate our need for further visibility to make our project feasible. The changes we have made are to add sensors in a star-burst pattern in the front and in the back of the vehicle. These sensors are both horizontal and angled downwards, this is to allow for the sensors to detect objects in its current path alongside with possible pot-holes or cliffs in the cars current path. In total we have implemented 20 sensors which we believe is sufficient for the current implementation of our software, adding more sensors than this would give diminishing return.
![](https://user-images.githubusercontent.com/90850693/170713803-b98161fc-a3a6-420c-a955-eb9f026f13c3.png)



### What kind of technology have we used?
**Runtime environment:** SMCE-Godot, Pixel 2 API 29 (Android 10).

**Map development:** Autodesk Maya and Godot.

**Car logic development language:** Arduino and C++.

**GUI development language:** Android Studio + Java.
