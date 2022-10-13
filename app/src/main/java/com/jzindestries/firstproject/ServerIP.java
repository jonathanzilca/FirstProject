package com.jzindestries.firstproject;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerIP {
    EditText IPAddress;
    Button update;
    public void showServerIP(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.server_ip);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;

        IPAddress = dialog.findViewById(R.id.editTextPhone);

        update = (Button) dialog.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SERVER_ADDRESS = String.valueOf(IPAddress.getText());
                Toast.makeText(activity, "IP updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });



        dialog.show();}
}
