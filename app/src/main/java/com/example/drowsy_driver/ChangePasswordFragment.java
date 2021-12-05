package com.example.drowsy_driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ChangePasswordFragment extends Fragment {
    EditText oldPassword;
    EditText newPassword;
    EditText confirmPassword;

    TextView displayEmail;

    Button button;

    String realPassword;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_change_password, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ((AppCompatActivity)(getActivity())).getSupportActionBar().setTitle("Change Password");
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user = FirebaseAuth.getInstance().getCurrentUser();

        oldPassword = v.findViewById(R.id.oldPassword);
        newPassword = v.findViewById(R.id.newPassword);
        confirmPassword = v.findViewById(R.id.confirmPassword);
        displayEmail = v.findViewById(R.id.email_address);

        ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 Account userAccount = snapshot.getValue(Account.class);

                 if (userAccount != null)
                 {
                     realPassword = userAccount.getPassword();
                     displayEmail.setText(userAccount.getEmail());
                 }
             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });

        button = v.findViewById(R.id.changePasswordButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oPass = oldPassword.getText().toString().trim();
                String nPass = newPassword.getText().toString().trim();
                String cPass = newPassword.getText().toString().trim();

                if (oPass.equals(realPassword))
                {
                    if (nPass.equals(cPass))
                    {
                        ref.child("password").setValue(nPass);
                        user.updatePassword(nPass);
                        Toast.makeText(getActivity(), "password changed", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });



       return v;
    }
}