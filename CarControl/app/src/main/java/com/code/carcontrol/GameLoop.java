package com.code.carcontrol;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * this class manages the UPS and FPS of the application and runs the loop that refreshes the app
 */

public class GameLoop extends Thread{
    /**
    *The following attributes are used to calculate the UPS and FPS and cap them to increse performace
     */
    public static final double MAX_UPS = 25.0;
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;
    private double averageUPS;
    private double averageFPS;

    /**
     *The following attributes are necessary to show the "game" content and place in on the surface
     * so the user can see it
     */
    private Game game;
    private SurfaceHolder surfaceHolder;

    /**
     *The following variable indicates if the app is running, it will be set to true when the game loop
     * starts
     */
    private boolean isRunning = false;

    /**
     * This is the constructor for the Gameloop class
     */

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }

    /**
     * The two following methods are used to get the average Frames Per Second and Updates Per Second elsewhere in the code.
     */
    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    /**
     * This methods starts the loop for running the game
     */

    public void startLoop() {
        Log.d("GameLoop.java", "startLoop()");
        isRunning = true;
        start();
    }

    /**
     *  Method to run the loop, it is optimized to provide 30 FPS and 30 UPS
     */

    @Override
    public void run() {
        Log.d("GameLoop.java", "run()");
        super.run();

        // Declare time and cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        // Game loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while(isRunning) {

            // Try to update and render game
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    game.update();
                    updateCount++;

                    game.draw(canvas);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Pause game loop to not exceed target UPS
            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = (long) (updateCount*UPS_PERIOD - elapsedTime);
            if(sleepTime > 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Skip frames to keep up with target UPS
            while(sleepTime < 0 && updateCount < MAX_UPS-1) {
                game.update();
                updateCount++;
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount*UPS_PERIOD - elapsedTime);
            }

            // Calculate average UPS and FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime >= 1000) {
                averageUPS = updateCount / (1E-3 * elapsedTime);
                averageFPS = frameCount / (1E-3 * elapsedTime);
                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}