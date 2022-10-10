package com.jzindestries.firstproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.Voice;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button answer,ignore;
    FloatingActionButton option1,option2,option3,voice;
    Animation scaleUp,scaleDown, fabOpen, fabClose, rotateForward, rotateBackward, voiceAppear, voiceDisappear;
    MenuBuilder menuBuilder;
    VideoView video;

    boolean isOpen = false;
    boolean isOn = false;
    boolean VOn = false;




    @SuppressLint("RestrictedApi")
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
        voice = findViewById(R.id.voice);
        video = findViewById(R.id.videoView);

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

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VOn){

                }else{voice.startAnimation(voiceDisappear);}

            }
        });

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
                System.exit(0);
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
                        switch (item.getItemId()){
                            case R.id.quickAnswer1:
                            case R.id.quickAnswer2:
                            case R.id.quickAnswer4:
                            case R.id.quickAnswer5:
                                Toast.makeText(MainActivity.this, "Quick message sent!", Toast.LENGTH_SHORT).show();
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