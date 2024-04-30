package android.danyk.DAO;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import android.danyk.R;
import android.danyk.dbManager.FirebaseSingleton;
import android.danyk.modelo.Ticket;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class TicketDAO {
    private final DatabaseReference databaseReference;
    private final Map<String, List<String>> userTicketIdMap;

    public TicketDAO() {
        databaseReference = FirebaseSingleton.getInstance().getDatabaseReference();
        userTicketIdMap = new HashMap<>();
    }

    public void insertarTicket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUris, boolean finalizado, String creadoPor, OnCompleteListener<Void> onCompleteListener) {
        String idTicket = generateRandomId();
        Ticket ticket = new Ticket(titulo, estado, prioridad, descripcion, imageUris, idTicket, finalizado, creadoPor);
        String id = databaseReference.child("ticket").push().getKey();
        assert id != null;
        databaseReference.child("ticket").child(id).setValue(ticket).addOnCompleteListener(onCompleteListener);
        TicketDAO ticketDAO = new TicketDAO();
        UserDAO userDAO = new UserDAO();
        ticketDAO.agregarMiTicket(userDAO.getUserID(),idTicket);
    }

    public void agregarTicketGuardado(String usuarioId, String ticketId) {
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

    public void eliminarTicketGuardado(String usuarioId, String ticketId) {
        DatabaseReference usuarioRef = databaseReference.child("usuarios").child(usuarioId);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtener la lista de idsGuardados del usuario
                    List<String> idsGuardados = new ArrayList<>();
                    DataSnapshot idsGuardadosSnapshot = dataSnapshot.child("idsGuardados");
                    for (DataSnapshot childSnapshot : idsGuardadosSnapshot.getChildren()) {
                        String idGuardado = childSnapshot.getValue(String.class);
                        if (!idGuardado.equals(ticketId)) {
                            // Agregar todos los idsGuardados excepto el que se va a eliminar
                            idsGuardados.add(idGuardado);
                        }
                    }

                    // Actualizar la lista de idsGuardados del usuario
                    usuarioRef.child("idsGuardados").setValue(idsGuardados);
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

    public void agregarMiTicket(String usuarioId, String ticketId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuarioRef = database.getReference().child("usuarios").child(usuarioId);

        // Verificar si el usuario existe en la base de datos
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El usuario existe, obtener la lista de idsGuardados
                    List<String> idsGuardados = new ArrayList<>();
                    DataSnapshot idsGuardadosSnapshot = dataSnapshot.child("idsCreados");
                    for (DataSnapshot childSnapshot : idsGuardadosSnapshot.getChildren()) {
                        String idGuardado = childSnapshot.getValue(String.class);
                        idsGuardados.add(idGuardado);
                    }

                    // Verificar si el ticket ya está guardado
                    if (!idsGuardados.contains(ticketId)) {
                        // El ticket no está guardado, agregarlo a la lista
                        idsGuardados.add(ticketId);
                        usuarioRef.child("idsCreados").setValue(idsGuardados);
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

    public void eliminarMiTicket(String usuarioId, String ticketId) {
        DatabaseReference usuarioRef = databaseReference.child("usuarios").child(usuarioId);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtener la lista de idsGuardados del usuario
                    List<String> idsGuardados = new ArrayList<>();
                    DataSnapshot idsGuardadosSnapshot = dataSnapshot.child("idsCreados");
                    for (DataSnapshot childSnapshot : idsGuardadosSnapshot.getChildren()) {
                        String idGuardado = childSnapshot.getValue(String.class);
                        if (!idGuardado.equals(ticketId)) {
                            // Agregar todos los idsGuardados excepto el que se va a eliminar
                            idsGuardados.add(idGuardado);
                        }
                    }

                    // Actualizar la lista de idsGuardados del usuario
                    usuarioRef.child("idsCreados").setValue(idsGuardados);
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

    public void eliminarTicketFinalizado(String ticketId, OnCompleteListener<Void> onCompleteListener) {
        DatabaseReference ticketRef = databaseReference.child("ticket").child(ticketId);
        ticketRef.removeValue().addOnCompleteListener(onCompleteListener);
    }
    public static String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
