package com.example.drowsy_driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DisplayPersonalInfoFragment extends Fragment {
    //final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference ref = database.getReference("Account");
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    Button deleteAccount;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;

    TextView name;
    TextView email;
    TextView vehicle;
    TextView password;

    String secretPassword;

    long timeStart;
    long timeEnd;
    
    float timeResult;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display_personal_info, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ((AppCompatActivity)(getActivity())).getSupportActionBar().setTitle("Profile");
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button editPI = v.findViewById(R.id.editPI);

        if (editPI != null){
            editPI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(DisplayPersonalInfoFragment.this).navigate(R.id.action_displayPersonalInfoFragment_to_personalInfoFragment);
                }
            });
        }

        deleteAccount = v.findViewById(R.id.delete);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(DisplayPersonalInfoFragment.this).navigate(R.id.action_displayPersonalInfoFragment_to_deleteAccountFragment);
            }
        });

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref.child("email").setValue(user.getEmail());

        //Toast.makeText(getActivity(), ref.getKey(), Toast.LENGTH_SHORT).show();

        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email_address);
        vehicle = v.findViewById(R.id.vehicleInfo);
        password = v.findViewById(R.id.password);

        if (user != null)
        {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Account userAccount = snapshot.getValue(Account.class);


                    if (userAccount != null)
                    {
                        //timing start of retrieving data
                        timeStart = System.currentTimeMillis();
                        name.setText("Name: \n" + userAccount.getFullName());
                        email.setText("Email Address: \n" + userAccount.getEmail());
                        vehicle.setText("Vehicle: \n" + userAccount.getVehicleInfo());
                        timeEnd = System.currentTimeMillis();
                        //timing end of retrieving data

                        timeResult = (timeEnd - timeStart);
                        //verbose output for time to retrieve data and from where
                        Log.v("DisplayPI", "Time to Retrieve Data: " + timeResult/1000 + " seconds");
                        Log.v("DisplayPI", "Data Retrieved From: " + ref.toString());

                        secretPassword = "";
                        for (int i = 0; i < userAccount.getPassword().length(); i++)
                        {
                            secretPassword = secretPassword + "*";
                        }
                        password.setText("Password: \n" + secretPassword);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return v;
    }
}