package com.example.drowsy_driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class PersonalInfoFragment extends Fragment {
    Button saveInfo;
    Button ChangePassword;

    Boolean flag;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;

    EditText editName;
    EditText editEmail;
    EditText editVehicleInfo;

    long timeStart;
    long timeEnd;

    float timeResultDatabase;
    float timeResultFirebase;
    float timeResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pi_settings, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ((AppCompatActivity)(getActivity())).getSupportActionBar().setTitle("Edit Personal Information");
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChangePassword = v.findViewById(R.id.ChangePassword);
        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PersonalInfoFragment.this).navigate(R.id.action_personalInfoFragment_to_changePasswordFragment);
            }
        });

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        editName = v.findViewById(R.id.name);
        editEmail = v.findViewById(R.id.email_address);
        editVehicleInfo = v.findViewById(R.id.vehicleInfo);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Account userAccount = snapshot.getValue(Account.class);

                    if (userAccount != null)
                    {
                        //timing account database retrieval
                        timeStart = System.currentTimeMillis();
                        editName.setText(userAccount.getFullName());
                        editEmail.setText(userAccount.getEmail());
                        editVehicleInfo.setText(userAccount.getVehicleInfo());
                        timeEnd = System.currentTimeMillis();

                        timeResult = timeEnd - timeStart;

                        Log.v("PersonalInfo", "Time to Retrieve Data: " + timeResult/1000 + " seconds");
                        Log.v("PersonalInfo", "Data Retrieved From: " + ref.toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            saveInfo = v.findViewById(R.id.SaveInformation);
            saveInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAccount();
                }
            });
        }

        return v;
    }

    private void setAccount()
    {
        flag = true;
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String vehicleInfo = editVehicleInfo.getText().toString().trim();

        if(name.isEmpty()){
            editName.setError("Full name is required");
            editName.requestFocus();
            flag = false;
            return;
        }
        if(email.isEmpty()){
            editEmail.setError("Email is Required");
            editEmail.requestFocus();
            flag = false;
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please provide valid email");
            editEmail.requestFocus();
            flag = false;
            return;
        }
        if(vehicleInfo.isEmpty()){
            editVehicleInfo.setError("Please tell us what kind of vehicle you drive");
            editVehicleInfo.requestFocus();
            flag = false;
            return;
        }

        //timing update database account
        timeStart = System.currentTimeMillis();
        ref.child("fullName").setValue(name);
        ref.child("email").setValue(email);
        ref.child("vehicleInfo").setValue(vehicleInfo);
        timeEnd = System.currentTimeMillis();
        timeResultDatabase = timeEnd - timeStart;

        //timing update firebase account
        timeStart = System.currentTimeMillis();
        user.updateEmail(email);
        timeEnd = System.currentTimeMillis();
        timeResultFirebase = timeEnd - timeStart;

        if (flag) {
            Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_SHORT).show();

            Log.v("PersonalInfo", "Time to Update Database Account: " + timeResultDatabase/1000);
            Log.v("PersonalInfo", "Database Account Updated: " + ref.toString());
            Log.v("PersonalInfo", "Time to Update Firebase User: " + timeResultFirebase/1000);
            Log.v("PersonalInfo", "Firebase User Updated: " + user.toString());
        }

    }
}