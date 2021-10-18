package com.example.drowsydriversettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Drowsy Driver asdfMenu");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Settings:
                startActivity(new Intent(this, MainSettings.class));
                return true;
            case R.id.item1:
                return true;
            case R.id.item2:
                return true;
            case R.id.sub_item1:
                return true;
            case R.id.sub_item2:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}