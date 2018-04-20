package com.zentere;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateServer extends Thread {


    private ServerSocket server = null;
    private DataInputStream in = null;
    private String response;
    private String topic = "app/1/camera/info";
    static private String broker_url = "tcp://localhost:1883";
    private MemoryPersistence persistence = new MemoryPersistence();
    private boolean retained = false;
    private static final String JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
    String publisherClientId = "publisher";

    private ServerSocket mServerSocket;
    protected List<ClientHandler> mClientHandlers;
    private Socket mSocket = null;

    public DateServer(int port) {
        try {
            this.mServerSocket = new ServerSocket(port);
            System.out.println("New server initialized!");
            mClientHandlers = Collections.synchronizedList(new ArrayList<ClientHandler>());
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket client = mServerSocket.accept();
                System.out.println(client.getInetAddress().getHostName() + " connected");
                ClientHandler newClient = new ClientHandler(client);
                mClientHandlers.add(newClient);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = client.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response = byteArrayOutputStream.toString("UTF-8");
                }
                System.out.println("" + response);
                inputStream.close();

                String[] event_details = response.split(",");

                String place = event_details[0];
                String hardwareAddress = event_details[1];
                String networkAddress = event_details[2];
                String filePath = event_details[3];
                String priority = event_details[4];
                String date = event_details[5];

                JSONObject eventObject = new JSONObject();
                eventObject.put("place", place);
                eventObject.put("hardwareaddress", hardwareAddress);
                eventObject.put("networkaddress", networkAddress);
                eventObject.put("filePath", filePath);
                eventObject.put("priority", priority);
                eventObject.put("date", date.replace("\n",""));

                new SendMessage(mClientHandlers,eventObject.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new DateServer(27001);
    }

}
