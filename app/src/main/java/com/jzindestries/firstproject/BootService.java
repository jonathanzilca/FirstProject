package com.jzindestries.firstproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class BootService extends BroadcastReceiver {
    private Thread socketThread;
    private Socket clientSocket;
    private InputStream inS = null;
    private Context context;

    // server settings
    private static String SERVER_ADDRESS = "192.168.1.204"; // make sure this matches whatever the server tells you
    private final int MSG_PORT = 4383;

    @Override
    public void onReceive(Context context, Intent intent) {
        socketThread = new Thread(get_nutify);
        this.context = context;
        if(intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "test of recieving", Toast.LENGTH_SHORT).show();
            socketThread.start();
        }
    // this code will run after the boot
    }

    private void display_notify(String msg){
        // notification code starts here
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My notification");
        builder.setSmallIcon(R.drawable.answer_icon_24);
        builder.setContentTitle("Intruder Alarm");
        builder.setContentText(msg);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(1, builder.build());
        // notification code ends here

    }

    private Runnable get_nutify = new Runnable() {
        @Override
        public void run() {
            try {
                clientSocket = new Socket(InetAddress.getByName(SERVER_ADDRESS), MSG_PORT);
                inS = clientSocket.getInputStream();
                // grab input stream of client socket
                byte[] packet = new byte[64];
                // recive data over the client socket
                while (inS != null && clientSocket.isConnected()) {
                    inS.read(packet,0,packet.length);
                    String data = new String(packet);
                    display_notify(data);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

}
