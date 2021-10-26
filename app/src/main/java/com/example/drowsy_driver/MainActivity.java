package com.example.drowsy_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button createAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openCreateAccount();
            }
        });

    }

    public void openCreateAccount(){
        Intent intent = new Intent(this, activity_create_account.class);
        startActivity(intent);
    }
}