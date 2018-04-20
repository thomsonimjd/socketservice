package com.zentere;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SendMessage extends  Thread {

    protected List<ClientHandler> clients;
    private String topic = "security_app/1/camera/info";
    static private String broker_url = "tcp://localhost:1883";
    private boolean retained = false;
    private static final String JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
    String publisherClientId = "publisher";
    private String  mPayload;

    public SendMessage(List<ClientHandler> clients,String response) {
        this.clients = clients;
         mPayload = response;
         this.start();
    }

    public void run() {
        System.out.println("New Communication Thread Started");
        try {
            publishEvent(mPayload);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void publishEvent(String content) throws UnsupportedEncodingException {
        try {
            MqttClient mqttPublisherClient = getNewMqttClient(publisherClientId);
            byte[] encodePayload = new byte[0];
            encodePayload = content.getBytes("UTF-8");
            mqttPublisherClient.publish(topic, encodePayload, QualityOfService.EXACTLY_ONCE.getValue(), retained);
            System.out.println("Message published");
            mqttPublisherClient.disconnect();
            System.out.println("Disconnected\n");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private static MqttClient getNewMqttClient(String clientId) throws MqttException {
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(JAVA_TMP_DIR + "/" + clientId);
        MqttClient mqttClient = new MqttClient(broker_url, clientId, dataStore);
        SimpleMQTTCallback callback = new SimpleMQTTCallback();
        mqttClient.setCallback(callback);
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        mqttClient.connect(connectOptions);
        return mqttClient;
    }

}
