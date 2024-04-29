package android.danyk.dbManager;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class UserDatabaseManager {
    private static final String USERS_NODE = "usuarios";
    private static final String CONFIG_NODE = "configuracion";
    private static final String PASSWORD_KEY = "passwordTecnico";

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

    public static void crearPasswordTecnico(String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference configRef = database.getReference().child(CONFIG_NODE);
        configRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    configRef.child(PASSWORD_KEY).setValue(password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error
            }
        });
    }

    public static String obtenerPasswordTecnico() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference configRef = database.getReference().child(CONFIG_NODE).child(PASSWORD_KEY);

        try {
            DataSnapshot dataSnapshot = Tasks.await(configRef.get());
            if (dataSnapshot.exists()) {
                return dataSnapshot.getValue(String.class);
            } else {
                // En caso de que la contraseña del técnico no esté configurada
                return null;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            // Manejar la excepción según sea necesario
            return null;
        }
    }


}
