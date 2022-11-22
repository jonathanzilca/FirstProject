package com.jzindestries.firstproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button answer,ignore, setting,voice, option1,option2,option3;
    Animation scaleUp,scaleDown, fabOpen, fabClose, rotateForward, rotateBackward, voiceAppear, voiceDisappear;
    MenuBuilder menuBuilder;
    ImageView liveStream;

    private static String SERVER_ADDRESS = "10.0.0.10"; // make sure this matches whatever the server tells you
    private static final int VIDEO_PORT = 4382;
    public static final int BUFFER_SIZE = 65000;
    public boolean answered = true;
    private Bitmap bm;
    private Thread socketThread;
    private Handler handler;
    private DatagramSocket ds;
    private String msg = "";
    private static SharedPreferences.Editor editor;
    private static MainActivity App;


    boolean isOpen = false;
    boolean isOn = false;
    boolean VOn = true;


    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("en"));
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String IP = sp.getString("ServerIP", "");
        if (IP.length() > 0 && Charset.forName("US-ASCII").newEncoder().canEncode(IP)) {
            SERVER_ADDRESS = IP;
            SocketForMsg.Change_Ip(IP);
        }
        editor = sp.edit();
        editor.clear();
        editor.commit();
        App = MainActivity.this;
        
// setting items from xml layout
        answer = findViewById(R.id.answer);
        ignore = findViewById(R.id.ignore);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        setting = findViewById(R.id.setting);
        voice = findViewById(R.id.voice);
        liveStream = findViewById(R.id.liveStream);

        // animations
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);

        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);

        voiceAppear = AnimationUtils.loadAnimation(this,R.anim.voice_appear);
        voiceDisappear = AnimationUtils.loadAnimation(this,R.anim.voice_disapear);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        // setting on ot off volume of live streaming
        liveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VOn){
                    voice.setBackgroundResource(R.drawable.ic_baseline_volume_up_24);
                    voice.startAnimation(voiceDisappear);
                    VOn = false;
                }else{
                    voice.setBackgroundResource(R.drawable.volume_off);
                    voice.startAnimation(voiceDisappear);
                    VOn = true;}
            }
        });


        // open 3 options for connection
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // notification code starts here
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notification");
                builder.setSmallIcon(R.drawable.answer_icon_24);
                builder.setContentTitle("Intruder Alarm");
                builder.setContentText("Click here to find out who's coming!");
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(1, builder.build());
                // notification code ends here
                animateFab();

            }
        });
        // getting out of app
        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeApp();
            }
        });

        // opening ip setting window
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerIP ServerIP = new ServerIP();
                ServerIP.showServerIP(MainActivity.this);
            }
        });

        // opening writing window
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteAndSend WriteAndSend = new WriteAndSend();
                WriteAndSend.showWriteAndSend(MainActivity.this);
            }
        });

        // changing call color and animation
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOn){
                option2.startAnimation(rotateBackward);
                option2.setBackgroundResource(R.drawable.button_options);
                option2.setPadding(0,50,0,0);
                isOn = false;
                } else{
                    option2.startAnimation(rotateForward);
                    option2.setBackgroundResource(R.drawable.call_back);
                    option2.setPadding(0,45,0,0);
                    Toast.makeText(MainActivity.this, "Call has started!", Toast.LENGTH_SHORT).show();
                    isOn = true;}
                }

        });

        // quick pop up menu answers
        menuBuilder = new MenuBuilder(this);
        MenuInflater inflater =  new MenuInflater(this);
        inflater.inflate(R.menu.popmenu, menuBuilder);
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuPopupHelper optionMenu = new MenuPopupHelper(MainActivity.this, menuBuilder,view);
                optionMenu.setForceShowIcon(false);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        SocketForMsg socketForVideo = new SocketForMsg();
                        Toast.makeText(MainActivity.this, "Quick message sent!", Toast.LENGTH_SHORT).show();
                        switch (item.getItemId()){
                            case R.id.quickAnswer1:
                                socketForVideo.sendMessage("Come in");
                                return true;
                            case R.id.quickAnswer2:
                                socketForVideo.sendMessage("Do not enter");
                                return true;
                            case R.id.quickAnswer3:
                                socketForVideo.sendMessage("Go away");
                                return true;
                            case R.id.quickAnswer4:
                                socketForVideo.sendMessage("Wait outside");
                                return true;
                            case R.id.quickAnswer5:
                                socketForVideo.sendMessage("I will come back soon");
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
                optionMenu.show();
            }
        });


    }
    
    public void closeApp(){
        MainActivity.this.finish();
        answered = false;
        try {
            socketThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity.this.finish();
        MainActivity.this.startActivity(MainActivity.this.getIntent());
    }

    public static void changeIP(String IP){
        if (IP.length() > 0 && Charset.forName("US-ASCII").newEncoder().canEncode(IP)) {
            System.out.println(IP);
            editor.putString("ServerIP", IP);
            editor.commit();
        }
        App.closeApp();
    }


    private Runnable vidUpdate  = new Runnable() {
        @Override
        public void run() {
            if(bm != null){
                liveStream.setImageBitmap(bm);
            }
        }
    };

    private Runnable closeSocket = new Runnable() {
        @Override
        public void run() {
            try {
//                DatagramSocket ds = new DatagramSocket();
                InetAddress ia = InetAddress.getByName(SERVER_ADDRESS);
                byte[] b = "close".getBytes(StandardCharsets.UTF_8);
                DatagramPacket dp = new DatagramPacket(b, b.length,ia,VIDEO_PORT);
                ds.send(dp);
                System.out.println("closed");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    private Runnable socketRun = new Runnable() {
        @Override
        public void run() {
            // configure client socket;
            try {
                ds = new DatagramSocket();
                byte[] b = "Hello".getBytes(StandardCharsets.UTF_8);

                InetAddress ia = InetAddress.getByName(SERVER_ADDRESS);
                DatagramPacket dp = new DatagramPacket(b, b.length,ia,VIDEO_PORT);
                ds.send(dp);
                ds.setSoTimeout(100);
                int c = 0;
                while (answered){
                    byte[] b1 = new byte[BUFFER_SIZE];
                    DatagramPacket dp1 = new DatagramPacket(b1, b1.length);

                    try{
                        ds.receive(dp1);

                        byte[] data = dp1.getData();
                        String text = new String(data, StandardCharsets.UTF_8);

                        if(dp1.getLength() == BUFFER_SIZE){
                            ds.receive(dp1);
                            byte[] tempdata1 = data;
                            byte[] tempdata2 = dp1.getData();
                            data = new byte[BUFFER_SIZE + dp1.getLength()];
                            System.arraycopy(tempdata1,0,data,0,BUFFER_SIZE);
                            System.arraycopy(tempdata2,0,data,BUFFER_SIZE,dp1.getLength());
                        }

                        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
                        bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        handler.post(vidUpdate);

                        c =0;
                    }
                    catch (IOException e) {
                        c++;
                        if(c > 50){
                            break;
                        }
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                b = "close".getBytes(StandardCharsets.UTF_8);

                ia = InetAddress.getByName(SERVER_ADDRESS);
                dp = new DatagramPacket(b, b.length,ia,VIDEO_PORT);
                ds.send(dp);

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
        answered = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pause");
        answered = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stop");
        answered = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
        answered = true;
        // establish connection between server and app
        socketThread = new Thread(socketRun);
        handler = new Handler();
        socketThread.start();
    }

    // animation of connection options
    private void animateFab(){
        if(isOpen){
            option1.startAnimation(fabClose);
            option2.startAnimation(fabClose);
            option3.startAnimation(fabClose);
            option1.setClickable(false);
            option2.setClickable(false);
            option3.setClickable(false);
            isOpen=false;
        }
        else{
            option1.startAnimation(fabOpen);
            option2.startAnimation(fabOpen);
            option3.startAnimation(fabOpen);
            option1.setClickable(true);
            option2.setClickable(true);
            option3.setClickable(true);
            isOpen=true;
        }
    }
}