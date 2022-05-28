# INSTALLATION GUIDE
The following document includes instructions on how to install the software developed by Group 13. 


# [Files](https://github.com/DIT113-V22/group-13)
- **Car Control Folder**: Contains all Java files that make up the application to control the car, In order to make use of it, its necessary to have installed **Android Studio** and select the emulator phone **Pixel 2 API 29**, as this model is compatible with **Paho Eclipse**, the preferred library for the use of MQTT
- **Arduino Folder**: Contains two different sketches of car models to simulate the **Arduino ESP32** inside of **SMCE-GODOT**, a simulator for Arduino Sketches developed by Gothenburg University Students. The folder with the name **MQTT_Joystick_Car** includes the software developed by the members of group 13 as a proof of concept of different assisted driving features, the **SmartCar** folder contains a basic sketch of the car with Serial-Based controls used to test the environments developed by the team.
- **Modifications Folder**: Contains all files used for 3D modelling for the **MQTT_Joystick_Car** sketch and the map based on **[Inom Vallgraven](https://www.google.com/maps/place/Inom+Vallgraven,+Gotemburgo/@57.7039778,11.9514769,14z/data=!4m5!3m4!1s0x464ff368a2410f03:0x71b0c1e1cef80fe1!8m2!3d57.7022551!4d11.9615896)** in the form of a PCK file

## Requirements to run the application
- Installation of all components should be completed
- Device should allow for the use of local host.
- Both the emulator and Car control application should connect to the same local host.

## [Application](https://github.com/DIT113-V22/group-13/wiki/Feature-2:-Graphical-user-interface)

### Steps to install and run aplication:
- Download [Android studio](https://developer.android.com/studio)
- Clone [repository](https://github.com/DIT113-V22/group-13) on GitHub desktop
- Set up [SDK](https://clevertap.com/blog/what-is-an-sdk/) 
- Select emulator phone Pixel 2 API 29
- Click run
>**Note**: due to deprecation of [eclipse paho](https://www.eclipse.org/paho/) it is necessary to use an emulator device with an API of 29 or lower


 ## Testing enviroment
 To get a better understanding of the different uses that this software offers, we developed a testing environment based on the neighbourhood of Gothenburg called **Inom Vallgraven** . When installed , the user can freely roam around the map, and test the assisted driving functionality in real life setting with moving obstacles
  ![alt text](https://github.com/DIT113-V22/group-13/blob/master/Documentation/images/Map.png)
### How to install the map
- Make a copy of the file [spatial.pck](https://github.com/DIT113-V22/group-13/blob/master/Modifications/Spatial.pck)
- Move that copy to the following path on windows: %appdata%\godot\app_userdata\smce\mods\
- For apple move it to the following path: ~/Library/Application\ Support/Godot/app_userdata/SMCE/mods
>**Note**: in newer versions of apple it is common that the finder does not show the contents inside of application support, if that were the case, you can use this command cp -R ~/Documents/group-13/Modifications/spatial.pck ~/Library/Application\ Support/Godot/app_userdata/SMCE/mods

## SMCE-GODOT
 SMCE-GODOT is the emulator developed by Gothenburg University students [Ruthger](https://github.com/RuthgerD) and [Ryan](https://github.com/AeroStun) to run Arduino sketches in a testing enviroment
 
### in order to make use of the sketch developed by our group you will need to complete the following steps:
- Download [SMCE-GODOT](https://github.com/ItJustWorksTM/smce-gd/tree/v1.3.3)
- Open the application
- Select the world spatial.pck in the top corner
- Select the sketch MQTT_Joystick_Car 
- Compile the sketch
- Press start
