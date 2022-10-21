package com.jzindestries.firstproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

public class ServerIP {
    TextView IPAddress;
    MenuBuilder menuBuilder;
    Button update, adding;
    @SuppressLint("RestrictedApi")
    public void showServerIP(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.server_ip);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;

        IPAddress = dialog.findViewById(R.id.ip_display);


        menuBuilder = new MenuBuilder(activity);
        MenuInflater inflater =  new MenuInflater(activity);
        inflater.inflate(R.menu.ip_options, menuBuilder);
        IPAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuPopupHelper optionMenu = new MenuPopupHelper(activity, menuBuilder,view);
                optionMenu.setForceShowIcon(false);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.add:
                                showAddDialog(activity);
                                return true;
                            default:
                                IPAddress.setText(item.getTitle());
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

        update = (Button) dialog.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeIP(IPAddress.getText().toString());
                Toast.makeText(activity, "IP updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void showAddDialog(Activity activity){
        final Dialog newDialog = new Dialog(activity);
        newDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newDialog.setCancelable(true);
        newDialog.setContentView(R.layout.add_ip);
        newDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;

        Button adding = newDialog.findViewById(R.id.adding);
        final EditText adding_ip = newDialog.findViewById(R.id.adding_ip);

        adding.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                String another_item = adding_ip.getText().toString();
                menuBuilder.add(another_item);
                newDialog.dismiss();

            }
        });

        newDialog.show();
    }


}
