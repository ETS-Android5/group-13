package com.code.carcontrol;

import org.junit.Test;
import java.util.Random;
import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;
import android.widget.Button;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JoystickActuatorTest {



    @Test
    public void getFixedJoystickOutput(){
        // create an array to store X and Y coordinates
        double[] coordinates;

        //initialize joystick and store coordinates after using it
        Joystick joystick = new Joystick(550, 500, 300, 200, true);
        coordinates = joystick.setActuator(551, 506);

        //test coordinates are accurate
        assertEquals(506,coordinates[1], 0);
        assertEquals(551,coordinates[0], 0);
    }

    @Test
    public void getRandomJoystickOutput(){
        // create an array to store X and Y coordinates
        double[] coordinates;

        //initialize joystick and store coordinates after using it with randomized coordinates
        Joystick joystick = new Joystick(550, 500,
                300, 200, true);
        Random random = new Random();
        int X = 500 + random.nextInt(300);
        int Y = 550 + random.nextInt(300);
        coordinates = joystick.setActuator(X, Y);

        //test coordinates are accurate
        assertEquals(Y,coordinates[1], 0);
        assertEquals(X,coordinates[0], 0);

        //repeat test for coordinates in the other half of the joystick
        X = 500 - random.nextInt(300);
        Y = 550 - random.nextInt(300);
        coordinates = joystick.setActuator(X, Y);
        //test coordinates are accurate
        assertEquals(Y,coordinates[1], 0);
        assertEquals(X,coordinates[0], 0);
    }


}