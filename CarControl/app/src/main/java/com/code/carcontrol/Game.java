package com.code.carcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.content.ContextCompat;

public class Game extends SurfaceView implements SurfaceHolder.Callback{
    private GameLoop gameLoop;
    Context context;
    private final Joystick joystick;
    /*
    this class manages all objects in the "Control Panel" and is responsible for updating all states
    and rendering all objects to the screen
     */

    public Game(Context context){

        super(context);

        //get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        this.context = context;

        //create and setup game loop
        gameLoop = new GameLoop(this, surfaceHolder);
        //Design of joystick: its position on the screen and size of outer-/inner circles
        joystick = new Joystick(1100, 450, 300, 200);
        setFocusable(true);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder){
        gameLoop.startLoop();
    }

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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }
    //Update circles & joystick
    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        drawFPS(canvas);
        drawUPS(canvas);
        joystick.draw(canvas);

    }

    public void drawUPS(Canvas canvas){
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.green);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: "+ averageUPS,100,40, paint);
    }

    public void drawFPS(Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.green);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: "+ averageFPS,100,80, paint);
    }

    public void update() {
        joystick.update();
    }
}
