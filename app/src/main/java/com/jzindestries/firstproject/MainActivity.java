package com.jzindestries.firstproject;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button answer,ignore, setting;
    FloatingActionButton option1,option2,option3,voice;
    Animation scaleUp,scaleDown, fabOpen, fabClose, rotateForward, rotateBackward, voiceAppear, voiceDisappear;
    MenuBuilder menuBuilder;
    ImageView liveStream;

    private static String SERVER_ADDRESS = "192.168.1.204"; // make sure this matches whatever the server tells you
    private final int SERVER_PORT = 4382;
    public static final int BUFFER_SIZE = 65507;
    public boolean answered = true;
    private Bitmap bm;
    private Thread socketThread;
    private Handler handler;
    DatagramSocket ds;

    boolean isOpen = false;
    boolean isOn = false;
    boolean VOn = false;



    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("en"));



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

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel("my notification", "my notification", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }

        liveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VOn){

                }else{voice.startAnimation(voiceDisappear);}

            }
        });

        // establish connection between server and app
        socketThread = new Thread(socketRun);
        socketThread.start();
        handler = new Handler();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // notification code starts here
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notification");
//                builder.setContentTitle("Door Protector");
//                builder.setContentText("Come and check out what has changed!");
//                builder.setAutoCancel(true);
//
//                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
//                managerCompat.notify(1, builder.build());
//                // notification code ends here
                animateFab();

            }
        });


        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
                answered = false;
//                new Thread(closeSocket).start();
                try {
                    socketThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                answered = true;
                System.exit(0);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // opening writing window
                ServerIP ServerIP = new ServerIP();
                ServerIP.showServerIP(MainActivity.this);
            }
        });


        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // opening writing window
                WriteAndSend WriteAndSend = new WriteAndSend();
                WriteAndSend.showWriteAndSend(MainActivity.this);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOn){
                option2.startAnimation(rotateBackward);
                //  changing color
                Drawable buttonDrawable = option2.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.blue));
                option2.setBackground(buttonDrawable);
                isOn = false;
                } else{
                    option2.startAnimation(rotateForward);
                    //  changing color
                    Drawable buttonDrawable = option2.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    //the color is a direct color int and not a color resource
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.green));
                    option2.setBackground(buttonDrawable);
                    Toast.makeText(MainActivity.this, "Call has started!", Toast.LENGTH_SHORT).show();
                    isOn = true;}
                }

        });

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
                        SocketForVideo socketForVideo = new SocketForVideo();
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

    private  Runnable vidUpdate  = new Runnable() {
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
                DatagramPacket dp = new DatagramPacket(b, b.length,ia,SERVER_PORT);
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
                DatagramPacket dp = new DatagramPacket(b, b.length,ia,SERVER_PORT);
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

                        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
                        bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        handler.post(vidUpdate);

//                        ds.receive(dp1);
//
//                        byte[] data = dp1.getData();
//                        if(dp1.getLength() == BUFFER_SIZE){
//                            ds.receive(dp1);
//                            byte[] tempdata1 = data;
//                            byte[] tempdata2 = dp1.getData();
//                            data = new byte[BUFFER_SIZE + dp1.getLength()];
//                            System.arraycopy(tempdata1,0,data,0,BUFFER_SIZE);
//                            System.arraycopy(tempdata2,0,data,BUFFER_SIZE,dp1.getLength());
//                        }
//                        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
//                        bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                        handler.post(vidUpdate);
                        c =0;
                    }
                    catch (IOException e) {
                        c++;
                        if(c > 100){
                            break;
                        }
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                b = "close".getBytes(StandardCharsets.UTF_8);

                ia = InetAddress.getByName(SERVER_ADDRESS);
                dp = new DatagramPacket(b, b.length,ia,SERVER_PORT);
                ds.send(dp);

                System.out.println("1");
                byte[] b1 = new byte[BUFFER_SIZE];
                DatagramPacket dp1 = new DatagramPacket(b1, b1.length);
                ds.receive(dp1);
                System.out.println("2");

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };

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