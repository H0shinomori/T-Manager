package android.danyk.dbManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSingleton {
    private static FirebaseSingleton instance;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private FirebaseSingleton() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseSingleton();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
