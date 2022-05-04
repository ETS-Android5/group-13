package com.code.carcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
/**
 * this class sets up the necessary components and runs the application when they are ready
 */

public class MainActivity extends AppCompatActivity {
    /*
        The following attributes are used for the MQTT connection
     */
    private static final String TAG = "SmartcarMqttController";
    private static final String EXTERNAL_MQTT_BROKER = "broker.emqx.io";
    private static final String PORT = ":1883";
    private static final String MQTT_SERVER = ("tcp://" + EXTERNAL_MQTT_BROKER + PORT);
    /*
        The following attributes are used for the broadcast on screen of the car's camera view
     */
    private static final int QOS = 1;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;

    /*ö
        The following attributes are used for the publishing of messages and reconnection to the
        server if needed
     */
    public static MqttClient mMqttClient;
    public static boolean isConnected = false;

    Button rotateLeft;
    Button rotateRight;
    Button cruiseControl;

    private boolean rotatingRight = false;
    private boolean rotatingLeft  = false;
    private boolean cruiseControlToggled = false;

    //creates the surface, client connection, window and sets the content on screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Draw out the XML design on to the canvas.
        setContentView(R.layout.activity_main);
        mMqttClient = new MqttClient(getApplicationContext(), MQTT_SERVER, TAG);
        //create a window
        Window window = getWindow();
        //make it full screen
        setContentView(R.layout.activity_main);
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Creates a reference to the LinearLayout in the xml sheet
        LinearLayout joystick = (LinearLayout) findViewById(R.id.joystick);
        // Clears it of any children
        joystick.removeAllViews();
        // Add Juan's  joystick object to this layout
        joystick.addView(new Game(this));

        //setContentView(new Game(this)); old game joystick only

        connectToMqttBroker();
         rotateLeft = (Button)findViewById(R.id.ROTATE_LEFT);
         rotateRight = (Button)findViewById(R.id.ROTATE_RIGHT);
         cruiseControl = (Button)findViewById(R.id.CRUISE_CONTROL);

        rotateLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN && rotatingLeft == false) {
                    mMqttClient.publish("DIT133Group13/RotateLeft", "1", 1,null);
                    rotatingLeft = true;
                    rotatingRight = false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mMqttClient.publish("DIT133Group13/RotateLeft", "0", 1,null);
                    rotatingLeft = false;
                }
                return false;
            }
        });

        rotateRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN && rotatingRight == false) {
                    mMqttClient.publish("DIT133Group13/RotateRight", "1", 1,null);
                    rotatingRight = true;
                    rotatingLeft = false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mMqttClient.publish("DIT133Group13/RotateRight", "0", 1, null);
                    rotatingRight = false;
                }

                return false;
            }
        });

        cruiseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cruiseControlToggled = !cruiseControlToggled;
                if (cruiseControlToggled) {
                    cruiseControl.setTextColor(Color.rgb(29,75,29));
                    mMqttClient.publish("DIT133Group13/CruiseControl", "1", 1,null);
                } else {
                    cruiseControl.setTextColor(Color.rgb(75,29,29));
                    mMqttClient.publish("DIT133Group13/CruiseControl", "0", 1,null);
                }
            }
        });
    }

    /**
     * This method, currently without usages, allows to resume the application and connection to mqtt
     * if it has been previously paused
     */
    @Override
    protected void onResume() {
        super.onResume();

        connectToMqttBroker();
    }

    /**
     * This method, currently without usages, allows to pause the application and connection to mqtt
     */
    @Override
    protected void onPause() {
        super.onPause();

        mMqttClient.disconnect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.i(TAG, "Disconnected from broker");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            }
        });
    }

    public void connectToMqttBroker() {
        if (!isConnected) {
            mMqttClient.connect(TAG, "", new IMqttActionListener() {
                /**
                 * Sub-Method to communicate via log when a connection is successful
                 * We include line 128 and 129 in case we want to broadcast the camera in the future
                 */
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnected = true;

                    final String successfulConnection = "Connected to MQTT broker";
                    Log.i(TAG, successfulConnection);
                    Toast.makeText(getApplicationContext(), successfulConnection, Toast.LENGTH_SHORT).show();

                    //mMqttClient.subscribe("/smartcar/ultrasound/front", QOS, null);
                    //mMqttClient.subscribe("/smartcar/camera", QOS, null);
                }

                /**
                 * Sub-Method to communicate via log when a connection is unsuccessful
                 */
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    final String failedConnection = "Failed to connect to MQTT broker";
                    System.out.println("FAILED CONNECTION: " + exception);
                    Log.e(TAG, failedConnection);
                    Toast.makeText(getApplicationContext(), failedConnection, Toast.LENGTH_SHORT).show();
                }
            }, new MqttCallback() {
                /**
                 * Sub-Method to communicate via log when the connection is lost
                 */
                @Override
                public void connectionLost(Throwable cause) {
                    isConnected = false;

                    final String connectionLost = "Connection to MQTT broker lost";
                    Log.w(TAG, connectionLost);
                    Toast.makeText(getApplicationContext(), connectionLost, Toast.LENGTH_SHORT).show();
                    connectToMqttBroker();
                }

                /**
                 * Sub-Method without current use that allows the application to recieve the broadcasting
                 * of the car's camera view.
                 */
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals("/smartcar/camera")) {
                        final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

                        final byte[] payload = message.getPayload();
                        final int[] colors = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
                        for (int ci = 0; ci < colors.length; ++ci) {
                            final byte r = payload[3 * ci];
                            final byte g = payload[3 * ci + 1];
                            final byte b = payload[3 * ci + 2];
                            colors[ci] = Color.rgb(r, g, b);
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

                    } else {
                        Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                    }
                }

                /**
                 * Sub-Method to indicate the successful delivery of a message
                 */
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //Log.d(TAG, "Message delivered");
                }
            });
        }
    }
}