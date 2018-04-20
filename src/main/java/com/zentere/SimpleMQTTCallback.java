package com.zentere;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SimpleMQTTCallback implements MqttCallback {

    /**
     * Inform when connection with server is lost.
     *
     * @param throwable Connection lost cause
     */
//    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Mqtt client lost connection with the server" + throwable);
    }

    /**
     * Inform when a message is received through a subscribed topic.
     *
     * @param topic       The topic message received from
     * @param mqttMessage The message received
     * @throws Exception
     */
//    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        //System.out.println("Message arrived on topic : \"" + topic + "\" Message : \"" + mqttMessage.toString() + "\"");
    }

    /**
     * Inform when message delivery is complete for a published message.
     *
     * @param iMqttDeliveryToken The message complete token
     */
//    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        for (String topic : iMqttDeliveryToken.getTopics()) {
            System.out.println("Message delivered successfully to topic : \"" + topic + "\".");
        }
    }

}