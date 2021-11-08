package com.example.drowsydriversettings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setTitle("Settings"); //title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adding back button
        if (getSupportActionBar() != null)
        {
            if (getSupportActionBar().getTitle().equals("Settings"))
            {
                NavigationUI.setupActionBarWithNavController(MainActivity.this, Navigation.findNavController(this, R.id.fragmentContainerView));
            }
        }

        /*
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
        */

    }





}