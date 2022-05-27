package com.code.carcontrol;

import static com.code.carcontrol.MainActivity.mMqttClient;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;

import java.sql.SQLOutput;

/** Explain more how this class works, how it is used and why it's used.
 This class includes features and methods of joystick.

 */
public class Joystick {

    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;
    private double lastSentPositionX = 550;
    private double lastSentPositionY = 500;

    private int outerCircleRadius;
    private int innerCircleRadius;

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    private boolean isPressed = false;
    private double joystickCenterToTouchDistance;
    private double actuatorX;
    private double actuatorY;

    /**
     * constructor with joystick coordinates
     * it initializes the coordinates of the outer, inmovable circle
     * and the inner, interactive circle that make up the joystick
     * The other lines of code are used to set the color for the joystick circles
     */
    public Joystick(int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius) {

        // Outer and inner circle make up the joystick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        // Radii of circles
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        // paint of circles
        outerCirclePaint = new Paint();
        //outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setColor(Color.rgb(100, 100, 100));
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        //innerCirclePaint.setColor(Color.BLUE);
        innerCirclePaint.setColor(Color.rgb(200, 200, 200));
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * method to get the displacement of the joystick in the Y axis to determine overall speed
     * It calculates the displacement on the y axis, substract the initial coordinates, and divide it
     * by 300 units, which is the maximum displacement and multiply by the top speed to get it proportional
     * to the displacement
     */

    /*  Even though the driver's joystick is located at a point which is slightly off from the upper Y-axis,
    we still can figure out that the driver's intention was to move the car move straight forward.
    To enable this, we divided the joystick to have 16 sections. Each section has a degree of (360/16),
    and as long as the driver moves the joystick within the same area, the movement in the code
    would be the same, adjusting the drivers movement to the central line of each section.
    Basic trigonometry was used to figure out the coordinates of section borders and the central lines.
    The code repetition is remained on purpose as it provides better understanding from a reader's perspective.*/

    public double getInnerX() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX * outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY * outerCircleRadius);

        double xDistanceFromCenter = Math.abs(550 - innerCircleCenterPositionX);
        double yDistanceFromCenter = Math.abs(500 - innerCircleCenterPositionY);
        double tangent = yDistanceFromCenter / xDistanceFromCenter;
        double newInnerCirclePositionX = 550;

        if (tangent > Math.tan(1.3744)) { //78.75
            newInnerCirclePositionX = 550.0;
        } else {
            newInnerCirclePositionX = innerCircleCenterPositionX;
        }
        return newInnerCirclePositionX;
    }


    public double getSpeed() {
        double innerCirclePositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
        double distanceY = 500 - innerCirclePositionY;
        double speed = distanceY / 150.00;
        speed *= 50;
        return speed;

    }



    /**
     * method to divide speed in right motor and left motor, and publish messages on MQTT
     */
    public void getSideSpeeds(){
        double innerCirclerPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        double distanceX =  550 -innerCirclerPositionX;
        double speed = this.getSpeed();
        double LeftSpeed = speed - speed * (distanceX/150);
        double RightSpeed = speed + speed * (distanceX/150);

        mMqttClient.publish("DIT133Group13/LeftSpeed", Double.toString(LeftSpeed), 1, null);
        mMqttClient.publish("DIT133Group13/RightSpeed", Double.toString(RightSpeed), 1, null);

    }

    /**
     * method to show the joystick on screen
     * joystick is made up of two circles
     */
    public void draw(Canvas canvas) {
        // Draw outer circle
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
        );

        // Draw inner circle
        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
        );
    }

    /**
     * the following two methods recalculate the innercircle position and is called in other methods to redraw
     * the joystick
     */
    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
    }

    //method to set joystick actuator

    /**
     * This method does the mathematical calculations needed to calculate the movement of the joystick
     * @param touchPositionX is the Y coordinate on screen of the joystick when dragged by a user
     * @param touchPositionY is the X coordinate on screen of the joystick when dragged by a user
     */
    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        if(deltaDistance < outerCircleRadius) {
            actuatorX = deltaX/outerCircleRadius;
            actuatorY = deltaY/outerCircleRadius;
            actuatorX = deltaX/2/outerCircleRadius;
            actuatorY = deltaY/2/outerCircleRadius;
        } else {
            actuatorX = deltaX/deltaDistance;
            actuatorY = deltaY/deltaDistance;
            actuatorX = deltaX / 2 / deltaDistance;
            actuatorY = deltaY / 2 / deltaDistance;
        }
    }
    //method to determine if joystick is being pressed

    /**
     * method that takes in the position of the user click/drag and determines if it is made inside of
     * the range of the inner joystick circle, returning true in that case
     */
    public boolean isPressed(double touchPositionX, double touchPositionY) {
        joystickCenterToTouchDistance = Math.sqrt(
                Math.pow(outerCircleCenterPositionX - touchPositionX, 2) +
                        Math.pow(outerCircleCenterPositionY - touchPositionY, 2)
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    /**
     * method that resets the joystick position to 0,0 if user input stops
     */
    public void resetActuator() {
        actuatorX = 0;
        actuatorY = 0;
    }
}
