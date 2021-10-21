package com.example.drowsydriversettings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean authorized = true;

        if (authorized){
            Button b = findViewById(R.id.button1);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent i = new Intent(MainActivity.this, MainScreen.class);
                    Intent i = MainScreen.makeIntent(MainActivity.this);
                    startActivity(i);
                    //finish(); //pressing back on home screen takes you out of app
                }
            });
        }


    }





}