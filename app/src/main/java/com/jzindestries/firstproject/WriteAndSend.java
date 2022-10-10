package com.jzindestries.firstproject;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WriteAndSend {
    Button sending;
        public void showWriteAndSend(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.write_and_send);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;

            sending = (Button) dialog.findViewById(R.id.sending);
            sending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Message sent!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });



            dialog.show();}
}
