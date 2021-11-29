package com.example.drowsy_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button createAccount;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private TextView forgotPassword;

    private FirebaseAuth mAuth;

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); //despite being MainActivity, it inflates activity_login bc activity_main holds the navHost

        signIn = (Button) findViewById(R.id.signInButton);
        signIn.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null)
        {
            if (getSupportActionBar().getTitle().equals("Settings"))
            {
                NavigationUI.setupActionBarWithNavController(MainActivity.this, Navigation.findNavController(this, R.id.fragmentContainerView));
            }
            else if (getSupportActionBar().getTitle().equals("Personal Information"))
            {
                NavigationUI.setupActionBarWithNavController(MainActivity.this, Navigation.findNavController(this, R.id.fragmentContainerView));
            }
        }
        /*
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
            }
        });

         */

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.createAccount:
                openCreateAccount();
                break;
            case R.id.signInButton:
                accountLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                overridePendingTransition(0, 0);
                break;
        }

    }

    private void accountLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
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

        if(password.length() < 6){
            editTextPassword.setError("Min password length is 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){ //successful log in
                        //redirect to main loop
                        startActivity(new Intent(MainActivity.this, camFunctionality.class)); //CHANGE TO MAIN CAMERA ACTIVITY
                        //setContentView(R.layout.activity_main); //this implements the navHost, cuz navHost is in activity main
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email for account verification", Toast.LENGTH_LONG).show();
                    }


                }else{
                    Toast.makeText(MainActivity.this, "Failed to Login!\nEmail or password may be incorrect.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void openCreateAccount(){
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}