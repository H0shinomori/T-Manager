package android.danyk.DAO;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.danyk.dbManager.FirebaseSingleton;
import android.danyk.modelo.Ticket;
import android.util.Log;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TicketDAO {
    private final DatabaseReference databaseReference;
    private final Map<String, List<String>> userTicketIdMap;

    public TicketDAO() {
        databaseReference = FirebaseSingleton.getInstance().getDatabaseReference();
        userTicketIdMap = new HashMap<>();
    }

    public void insertarTicket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUris, OnCompleteListener<Void> onCompleteListener) {
        String idTicket = generateRandomId();
        Ticket ticket = new Ticket(titulo, estado, prioridad, descripcion, imageUris, idTicket);
        String id = databaseReference.child("ticket").push().getKey();
        assert id != null;
        databaseReference.child("ticket").child(id).setValue(ticket).addOnCompleteListener(onCompleteListener);
    }

    public static void agregarTicketGuardado(String usuarioId, String ticketId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuarioRef = database.getReference().child("usuarios").child(usuarioId);

        // Verificar si el usuario existe en la base de datos
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El usuario existe, obtener la lista de idsGuardados
                    List<String> idsGuardados = new ArrayList<>();
                    DataSnapshot idsGuardadosSnapshot = dataSnapshot.child("idsGuardados");
                    for (DataSnapshot childSnapshot : idsGuardadosSnapshot.getChildren()) {
                        String idGuardado = childSnapshot.getValue(String.class);
                        idsGuardados.add(idGuardado);
                    }

                    // Verificar si el ticket ya está guardado
                    if (!idsGuardados.contains(ticketId)) {
                        // El ticket no está guardado, agregarlo a la lista
                        idsGuardados.add(ticketId);
                        usuarioRef.child("idsGuardados").setValue(idsGuardados);
                    }
                } else {
                    // El usuario no existe en la base de datos
                    // Manejar según sea necesario
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error
            }
        });
    }


    public static String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
