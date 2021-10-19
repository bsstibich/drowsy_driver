package com.example.drowsydriversettings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class MainSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        /*
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        MainPreferenceFragment mainPreferenceFragment = new MainPreferenceFragment();
        fragmentTransaction.replace(android.R.id.content, mainPreferenceFragment);
        fragmentTransaction.commit();
        */

        if (findViewById(R.id.MainPreferenceFragment_container) != null)
        {
            if (savedInstanceState != null) return;

            getFragmentManager().beginTransaction().add(R.id.MainPreferenceFragment_container, new MainPreferenceFragment()).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbarSettings); //declaring and specifying toolbar
        setSupportActionBar(toolbar); //setting tool bar

        getSupportActionBar().setTitle("Settings"); //title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adding back button

    }

    @Override
    public boolean onSupportNavigateUp() { //go back a page; possible through "getSupportActionBar().setDisplayHomeAsUpEnabled(true);"
        finish();
        return true;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creates tool bar drop down options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    */
}