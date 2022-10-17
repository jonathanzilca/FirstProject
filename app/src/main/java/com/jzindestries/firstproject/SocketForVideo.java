package com.jzindestries.firstproject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketForVideo {


    // server settings
    private static String SERVER_ADDRESS = "10.0.0.10"; // make sure this matches whatever the server tells you
    private final int SERVER_PORT = 4381;
    private String msg = "";


    // client settings
    private Socket clientSocket;
    private OutputStream outS = null;

    protected void sendMessage(String msg) {
        this.msg = msg;
        new Thread(socketThread).start();
    }

    public static void Change_Ip(String ip){
        SERVER_ADDRESS = ip;
    }

    private Runnable socketThread = new Runnable() {
        @Override
        public void run() {
            // configure client socket
            try {
                clientSocket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
                outS = clientSocket.getOutputStream();
                // grab output stream of client socket
                byte[] packet = msg.getBytes(StandardCharsets.UTF_8);
                // send data over the client socket
                if (outS != null && clientSocket.isConnected()) {
                    try {
                        outS.write(packet);
                        outS.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

}
