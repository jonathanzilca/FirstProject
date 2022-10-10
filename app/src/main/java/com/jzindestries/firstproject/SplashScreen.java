package com.jzindestries.firstproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    Handler h = new Handler();
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("en"));

        image = findViewById(R.id.image);
        image.animate().translationY(-620).setDuration(2000).setStartDelay(0);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        },4000);

    }
}