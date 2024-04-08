package android.danyk.DAO;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    public void addTicketIdForUser(String userId, String ticketId) {
        List<String> userTicketIds = userTicketIdMap.computeIfAbsent(userId, k -> new ArrayList<>());
        if (!userTicketIds.contains(ticketId)) {
            userTicketIds.add(ticketId);
        }
    }

    public static String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
