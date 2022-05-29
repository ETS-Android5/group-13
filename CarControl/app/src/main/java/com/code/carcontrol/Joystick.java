package com.code.carcontrol;

import static com.code.carcontrol.MainActivity.mMqttClient;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Joystick {
    /**
     * These are variables to calculate the position of the joystick
     */
    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;
    private double lastSentPositionX = 550;
    private double lastSentPositionY = 500;
    /**
     * These are variables to set the radius of the joystick
     */
    private int outerCircleRadius;
    private int innerCircleRadius;
    /**
     * These are variables to choose font and size of text on the joystick
     */
    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    /**
     * This is a variable to check if the joystick is being pressed down
     */
    private boolean isPressed = false;
    /**
     * This variable is used to calculate the displacement of the joystick
     */
    private double joystickCenterToTouchDistance;
    /**
     * This are variables to keep track of the joysticks movement
     */
    private double actuatorX;
    private double actuatorY;

    /**
     * variable to keep track of joystick for unit testing
     */
    public static double lastCoordinateX;
    public static double lastCoordinateY;

    /**
     * constructor with joystick coordinates
     * it initializes the coordinates of the outer, inmovable circle
     * and the inner, interactive circle that make up the joystick
     * The other lines of code are used to set the color for the joystick circles
     */
    public Joystick(int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius, boolean test) {

        // Outer and inner circle make up the joystick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        // Radius of the circles
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;
        if (!test){
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
    }

    /**
     * method to get the displacement of the joystick in the Y axis to determine overall speed
     * It calculates the displacement on the y axis, subtract the initial coordinates, and divide it
     * by 300 units, which is the maximum displacement and multiply by the top speed to get it proportional
     * to the displacement
     */

    public double getInnerX() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX * outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY * outerCircleRadius);

        double xDistanceFromCenter = Math.abs(550 - innerCircleCenterPositionX);
        double yDistanceFromCenter = Math.abs(500 - innerCircleCenterPositionY);
        double tangent = yDistanceFromCenter / xDistanceFromCenter;
        double newInnerCirclePositionX = 550;
        // This is to keep the car going straight forwards even if the joystick is slightly off center in the y-axis
        if (tangent > Math.tan(1.3744)) { //78.75
            newInnerCirclePositionX = 550.0;
        } else {
            newInnerCirclePositionX = innerCircleCenterPositionX;
        }
        return newInnerCirclePositionX;
    }
    /**
     * This method is used to get the displacement of the joystick and returns the speed
     */

    public double getSpeed() {
        double innerCirclePositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
        double distanceY = 500 - innerCirclePositionY;
        double speed = distanceY / 150.00;
        speed *= 50;
        Game.speed = speed;

        return speed;

    }

    /**
     * Method to divide speed in right motor and left motor, and publish messages on MQTT
     */
    public void getSideSpeeds(){
        double innerCirclePositionX = getInnerX();
        double innerCirclePositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
        double distanceX =  550 -innerCirclePositionX;
        double speed = this.getSpeed();
        double LeftSpeed = speed - speed * (distanceX/150);
        double RightSpeed = speed + speed * (distanceX/150);
        // This variable is used to not spam the broker with messages that contain too similar information
        double requiredChange = 10;

        // This is checking if the current change is greater than the required change to send the message to Mqtt
        if ((Math.abs(innerCirclePositionX-lastSentPositionX) > requiredChange) || (Math.abs(innerCirclePositionY-lastSentPositionY) > requiredChange)) {
            mMqttClient.publish("DIT133Group13/Speed", Double.toString(LeftSpeed) + "/" +  Double.toString(RightSpeed), 1, null);
            lastSentPositionX = innerCirclePositionX;
            lastSentPositionY = innerCirclePositionY;
        }
    }

    /**
     * Method to show the joystick on screen, the joystick is made up of two circles
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

    /**
     * Method to set joystick actuator
     * This method does the mathematical calculations needed to calculate the movement of the joystick
     * @param touchPositionX is the Y coordinate on screen of the joystick when dragged by a user
     * @param touchPositionY is the X coordinate on screen of the joystick when dragged by a user
     */
    public double[] setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double[] coordinates;
        coordinates = new double[2];
        coordinates[0] = touchPositionX;
        coordinates[1] = touchPositionY;
        // If-else statement to maintain functionality even if the inner circle is being dragged outside of the outer circle
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
        return coordinates;
    }

    /**
     * Method that takes in the position of the user click/drag and determines if it is made inside of
     * the range of the inner joystick circle, returning true in that case
     */
    public boolean isPressed(double touchPositionX, double touchPositionY) {
        joystickCenterToTouchDistance = Math.sqrt(
                Math.pow(outerCircleCenterPositionX - touchPositionX, 2) +
                        Math.pow(outerCircleCenterPositionY - touchPositionY, 2)
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    /**
     * Getters and setters for the isPressed method
     */
    public boolean getIsPressed() {
        return isPressed;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    /**
     * Method that resets the joystick position to 0,0 if user input stops
     */
    public void resetActuator() {
        actuatorX = 0;
        actuatorY = 0;
    }
}
