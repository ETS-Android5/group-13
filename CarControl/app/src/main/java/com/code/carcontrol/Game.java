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
    /*
    Explain what these attributes do
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
        joystick = new Joystick(550, 500, 300, 200);
        setFocusable(true);
    }

    /**
     * Explain what this method does
     * @param holder What this does
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        gameLoop.startLoop();
    }

    /**
     * Explain what hits method does
     * @param event what this does
     * @return what is returned
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
     * Explain what this does and what will be in it
     * @param holder What this is
     * @param format What this is
     * @param width What this is
     * @param height What this is
     */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    /**
     * Explain what this does and what will be in it
     * @param holder what this is
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    /**
     * Update circles & joystick
     * @param canvas what this is
     */

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(Color.parseColor("#555555"));
        drawFPS(canvas);
        drawUPS(canvas);
        joystick.draw(canvas);
    }

    // We should now have UPS drawn on canvas / William
    public void drawUPS(Canvas canvas){
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.green);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: "+ averageUPS,100,40, paint);
    }

    // We should now have FPS drawn on canvas / William
    public void drawFPS(Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.green);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: "+ averageFPS,100,80, paint);
    }

    /**
     * Explain what this method does
     */
    public void update() {
        if(isConnected){
            joystick.getSideSpeeds();
        }
        joystick.update();
    }
}
