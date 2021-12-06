package com.example.drowsy_driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DeleteAccountFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;

    String email;
    String password;
    String realPassword;

    TextView displayEmail;
    EditText editPassword;

    Button delete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View w = inflater.inflate(R.layout.fragment_delete_account, container, false);


        Toolbar toolbar = w.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ((AppCompatActivity)(getActivity())).getSupportActionBar().setTitle("Delete Profile");
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();

        ref = database.getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String databaseAccountName = ref.toString();

        user = FirebaseAuth.getInstance().getCurrentUser();
        String firebaseAccountName = user.toString();

        displayEmail = w.findViewById(R.id.email_address);

        if (user != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Account userAccount = snapshot.getValue(Account.class);
                    if (userAccount != null)
                    {
                        email = userAccount.getEmail();
                        realPassword = userAccount.getPassword();
                        displayEmail.setText(email);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });


            delete = w.findViewById(R.id.deleteAccount);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPassword = w.findViewById(R.id.password);
                    password = editPassword.getText().toString().trim();

                    if (realPassword.equals(password)) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        ref.removeValue();
                        user.delete();

                        Log.v("DeleteAccount", "Database Account Deleted: " + databaseAccountName);
                        Log.v("DeleteAccount", "Firebase User Account Deleted: " + firebaseAccountName);
                        getActivity().finish();
                    }

                }
            });
        }

        return w;
    }
}