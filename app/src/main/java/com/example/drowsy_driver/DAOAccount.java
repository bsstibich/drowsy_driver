package com.example.drowsy_driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOAccount {
    private DatabaseReference databaseReference;

    public DAOAccount(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Account.class.getSimpleName());
    }
    public Task<Void> add(Account acc){
        return databaseReference.push().setValue(acc);
    }

}
