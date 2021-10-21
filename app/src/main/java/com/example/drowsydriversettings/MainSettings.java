package com.example.drowsydriversettings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainSettings extends AppCompatActivity {
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    public String driverType;

    private final SharedPreferences.OnSharedPreferenceChangeListener sharedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            driverType = sharedPreferences.getString(key, "default");
            //editor = sharedPreferences.edit();
            if (driverType.equals("Commuter") || driverType.equals("Road Tripper") || driverType.equals("Truck Driver")) {
                Toast.makeText(MainSettings.this, driverType, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        if (findViewById(R.id.MainPreferenceFragment_container) != null)
        {
            if (savedInstanceState != null) return;

            getFragmentManager().beginTransaction().add(R.id.MainPreferenceFragment_container, new MainPreferenceFragment()).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbarSettings); //declaring and specifying toolbar
        setSupportActionBar(toolbar); //setting tool bar

        getSupportActionBar().setTitle("Settings"); //title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adding back button
        /*
        Preference.OnPreferenceChangeListener ears = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return false;
            }
        };
        */
        //PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(sharedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(sharedListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(sharedListener);
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