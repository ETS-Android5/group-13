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

    /** Explain what htis method does
     *
     * @param username what thsi is
     * @param password what htis is
     * @param connectionCallback what this is
     * @param clientCallback what this is
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
     * Explain what htis method does
     * @param disconnectionCallback what this is
     */

    public void disconnect(IMqttActionListener disconnectionCallback) {
        try {
            mMqttAndroidClient.disconnect(null, disconnectionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Explain what this method does
     * @param topic what this is
     * @param qos what this is
     * @param subscriptionCallback what this is
     */
    public void subscribe(String topic, int qos, IMqttActionListener subscriptionCallback) {
        try {
            mMqttAndroidClient.subscribe(topic, qos, null, subscriptionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Explain when this method is used what it does
     * @param topic what this is
     * @param unsubscriptionCallback what htis is
     */

    public void unsubscribe(String topic, IMqttActionListener unsubscriptionCallback) {
        try {
            mMqttAndroidClient.unsubscribe(topic, null, unsubscriptionCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Explain what this method does
     * @param topic what this is
     * @param message what this is
     * @param qos what this is
     * @param publishCallback what htis is
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



