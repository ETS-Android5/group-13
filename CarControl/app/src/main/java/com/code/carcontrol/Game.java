package com.code.carcontrol;

import static com.code.carcontrol.MainActivity.isConnected;
import static com.code.carcontrol.MainActivity.mMqttClient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * this class manages all objects in the "Control Panel" and is responsible for updating all states
 * and rendering all objects to the screen
 */

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    /**
     * attributes used to keep the application in a loop and initialize the joystick
     */
    private GameLoop gameLoop;
    Context context;
    private final Joystick joystick;


    public Game(Context context){

        super(context);

        //get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        this.context = context;

        //create and setup game loop
        gameLoop = new GameLoop(this, surfaceHolder);
        //Design of joystick: its position on the screen and size of outer-/inner circles
        //joystick = new Joystick(1100, 450, 300, 200); OLD

        // This places the joystick in the center position of the smaller LinearView
        joystick = new Joystick(550, 500, 300, 200, false);
        setFocusable(true);
    }

    /**
     * Method that starts the loop once the surface is created
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        gameLoop.startLoop();
    }

    /**
     * Method to gather the coordinates of the joystick when pressed and moved by the user
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (joystick.isPressed((double)event.getX(),(double)event.getY())){
                    joystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (joystick.getIsPressed()){
                    joystick.setActuator((double)event.getX(),(double)event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                joystick.setIsPressed(false);
                joystick.resetActuator();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * The following methods are obligatory to override in order to use the interface, but no new
     * functionality is added in them
     */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    /**
     * This three methods draw the joystick, FPS and UPS on the canvas (screen output)
     */

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(Color.parseColor("#555555"));
        drawFPS(canvas);
        drawUPS(canvas);
        joystick.draw(canvas);
    }
    public void drawUPS(Canvas canvas){
        String UPS = Double.toString(gameLoop.getAverageUPS());
        UPS = UPS.substring(0,2);
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.lightBlack);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: "+ UPS,100,150, paint);
    }
    public void drawFPS(Canvas canvas){
        String FPS = Double.toString(gameLoop.getAverageFPS());
        FPS = FPS.substring(0,2);
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.lightBlack);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: "+ FPS,100,95, paint);
    }

    /**
     * this method is called repeately to update the game state
     * joystick.update() updates the functionality and position of the joystick
     * joystick.getSideSpeeds() gathers the positioning of the joystick and sends user input to the
     * MQTT server
     */
    public void update() {
        if(isConnected){
            joystick.getSideSpeeds();
        }
        joystick.update();
    }
}
