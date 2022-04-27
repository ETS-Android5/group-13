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

    private int outerCircleRadius;
    private int innerCircleRadius;

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    private boolean isPressed = false;
    private double joystickCenterToTouchDistance;
    private double actuatorX;
    private double actuatorY;

    /** Explain waht this method does
     * constructor with joystick coordinates
     * @param centerPositionX What is this parameter
     * @param centerPositionY What is this parameter
     * @param outerCircleRadius What is this parameter
     * @param innerCircleRadius What is this parameter
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
     * @return explain what is returned
     */
    public double getSpeed() {
        double innerCirclePositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
        double distanceY = 450 - innerCirclePositionY;
        double speed = distanceY / 300.00;
        speed *= 50;
        return speed;
    }

    /**
     * method to divide speed in right motor and left motor, and publish messages on MQTT
     */
    public void getSideSpeeds(){
        double innerCirclerPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        double distanceX =  1100 -innerCirclerPositionX;
        System.out.println("updating");
        double speed = this.getSpeed();
        double LeftSpeed = speed - speed * (distanceX/300);
        double RightSpeed = speed + speed * (distanceX/300);
        mMqttClient.publish("DIT133Group13/LeftSpeed", Double.toString(LeftSpeed), 1,null);
        mMqttClient.publish("DIT133Group13/RightSpeed", Double.toString(RightSpeed), 1,null);
        System.out.println("left: "+ LeftSpeed + " right: "+ RightSpeed);
    }

    /**
     * method to show the joystick on screen
     * joystick is made up of two circles
     * @param canvas Explain what this parameter is used for
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
     * Explaion what this method does here
     */
    public void update() {
        updateInnerCirclePosition();
    }

    //method to update joystick position when clicked on

    /**
     * Explaion what this method does here
     */
    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
    }

    //method to set joystick actuator

    /**
     * Explain what this method does here
     * @param touchPositionX explain what this parameter is
     * @param touchPositionY explain what this parameter is
     */
    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));


         // Made it move more natural than before // William
        // remove 121, 122, 126 and 127 to reverse it.
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
     *
     * @param touchPositionX // Explain what this is here
     * @param touchPositionY // Explain what this is here
     * @return // What is returned?
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
     * Explain what this method is for
     */
    public void resetActuator() {
        actuatorX = 0;
        actuatorY = 0;
    }
}
