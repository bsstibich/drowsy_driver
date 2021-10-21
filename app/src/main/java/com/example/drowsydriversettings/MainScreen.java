package com.example.drowsydriversettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainScreen extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainScreen.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);



        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Drowsy Driver Menu");

        FloatingActionButton fab = findViewById(R.id.settings_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreen.this, MainSettings.class));
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creates tool bar drop down options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.LogOut:
                finish();
                return true;
            case R.id.item1:
                return true;
            case R.id.sub_item1:
                return true;
            case R.id.sub_item2:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}