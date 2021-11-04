package com.example.drowsy_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class activity_create_account extends AppCompatActivity implements View.OnClickListener{
    private Button cancelButton, createAccount;
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextUserName, editTextFullName, editTextPassword, editTextConfirmPassword, editTextVehicleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        EditText editTextEmail = findViewById(R.id.email);
        EditText editTextUserName = findViewById(R.id.userName);
        EditText editTextFullName = findViewById(R.id.fullName);
        EditText editTextPassword = findViewById(R.id.password);
        EditText editTextConfirmPassword = findViewById(R.id.confirmPassword);
        EditText editTextVehicleInfo = findViewById(R.id.vehicleInfo);


        /*
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
        */

    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.cancelButton:
                //startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.createAccount:
                createAccount();
                break;
        }

    }

    private void createAccount() {
        EditText editTextEmail = findViewById(R.id.email);
        EditText editTextUserName = findViewById(R.id.userName);
        EditText editTextFullName = findViewById(R.id.fullName);
        EditText editTextPassword = findViewById(R.id.password);
        EditText editTextConfirmPassword = findViewById(R.id.confirmPassword);
        EditText editTextVehicleInfo = findViewById(R.id.vehicleInfo);


        String email = editTextEmail.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String vehicleInfo = editTextVehicleInfo.getText().toString().trim();

        if(userName.isEmpty()){
            editTextUserName.setError("Username is required");
            editTextUserName.requestFocus();
            return;
        }
        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty()){
            editTextConfirmPassword.setError("Please confirm your password is correct");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if(!password.equals(confirmPassword)){
            editTextConfirmPassword.setError("Your passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("Min password length is 6 characters");
            editTextPassword.requestFocus();
            return;
        }
        if(vehicleInfo.isEmpty()){
            editTextVehicleInfo.setError("Please tell us what kind of vehicle you drive");
            editTextVehicleInfo.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Account account = new Account(email, userName, fullName, password, vehicleInfo);

                            FirebaseDatabase.getInstance().getReference("Accounts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(activity_create_account.this, "Account has been created", Toast.LENGTH_LONG).show();

                                        // redirect to login layout
                                    }else{
                                        Toast.makeText(activity_create_account.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }else{
                            Toast.makeText(activity_create_account.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });



    }
}