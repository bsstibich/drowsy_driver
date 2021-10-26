package com.example.drowsy_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class activity_create_account extends AppCompatActivity {
    private Button cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final EditText email = findViewById(R.id.email);
        final EditText userName = findViewById(R.id.userName);
        final EditText fullName = findViewById(R.id.fullName);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.confirmPassword);
        final EditText vehicleInfo = findViewById(R.id.vehicleInfo);
        DAOAccount dao = new DAOAccount();

        Button createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(v->{
            Account acc = new Account(email.getText().toString(), userName.getText().toString(), fullName.getText().toString(), password.getText().toString(), vehicleInfo.getText().toString());
            dao.add(acc).addOnSuccessListener(suc->{
                Toast.makeText(this,"Account Created!", Toast.LENGTH_SHORT).show();
                openActivityMain();
            }).addOnFailureListener(er->{
                Toast.makeText(this,""+er.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openActivityMain();
            }

        });
    }
    public void openActivityMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}