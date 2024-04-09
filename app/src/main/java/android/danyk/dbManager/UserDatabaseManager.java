package android.danyk.dbManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDatabaseManager {
    private static final String USERS_NODE = "usuarios";

    public static void crearUsuarioSiNoExiste(String usuarioId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuarioRef = database.getReference().child(USERS_NODE).child(usuarioId);

        // Verificar si el usuario ya existe en la base de datos
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // El usuario no existe, crearlo con dos nodos para idsGuardados e idsCreados
                    usuarioRef.child("usuarioId").setValue(usuarioId);
                    usuarioRef.child("idsGuardados").setValue(new ArrayList<String>());
                    usuarioRef.child("idsCreados").setValue(new ArrayList<String>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error
            }
        });
    }


}
