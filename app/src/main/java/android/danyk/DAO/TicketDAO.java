package android.danyk.DAO;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;

import android.danyk.dbManager.FirebaseSingleton;
import android.danyk.modelo.Ticket;

public class TicketDAO {
    private final DatabaseReference databaseReference;

    public TicketDAO() {
        databaseReference = FirebaseSingleton.getInstance().getDatabaseReference();
    }

    public void insertarTicket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUris, OnCompleteListener<Void> onCompleteListener) {
        Ticket ticket = new Ticket(titulo, estado, prioridad, descripcion, imageUris);
        String id = databaseReference.child("ticket").push().getKey();
        databaseReference.child("ticket").child(id).setValue(ticket).addOnCompleteListener(onCompleteListener);
    }

    public void subirURlImagen(Map<String, Object> ticketData, OnCompleteListener<Void> onCompleteListener) {
        String ticketId = (String) ticketData.get("ticketId");

        if (ticketId != null) {
            databaseReference.child("ticket").child(ticketId).updateChildren(ticketData).addOnCompleteListener(onCompleteListener);
        }
    }
}

