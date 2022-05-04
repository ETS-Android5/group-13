package com.code.carcontrol;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

/**
 * This class contains the methods to connect, disconnect, subscribe, unsubscribe and
 * publish in mqtt
 */
public class MqttClient {
    private MqttAndroidClient mMqttAndroidClient;

    public MqttClient(Context context, String serverUrl, String clientId) {
        mMqttAndroidClient = new MqttAndroidClient(context, serverUrl, clientId);
    }

    /**
     * This method connects to the MQTT client using credentials
     */
    public void connect(String username, String password, IMqttActionListener connectionCallback, MqttCallback clientCallback) {
        mMqttAndroidClient.setCallback(clientCallback);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        try {
            mMqttAndroidClient.connect(options, null, connectionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method disconnects from the MQTT client
     */

    public void disconnect(IMqttActionListener disconnectionCallback) {
        try {
            mMqttAndroidClient.disconnect(null, disconnectionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to subscribe to a MQTT topic and disable method that act on the event listener
     */
    public void subscribe(String topic, int qos, IMqttActionListener subscriptionCallback) {
        try {
            mMqttAndroidClient.subscribe(topic, qos, null, subscriptionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to unsubscribe from a topic in a MQTT server
     */

    public void unsubscribe(String topic, IMqttActionListener unsubscriptionCallback) {
        try {
            mMqttAndroidClient.unsubscribe(topic, null, unsubscriptionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to public a MQTT message made up of a topic and a string
     */

    public void publish(String topic, String message, int qos, IMqttActionListener publishCallback) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttMessage.setQos(qos);

        try {
            mMqttAndroidClient.publish(topic, mqttMessage, null, publishCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}



