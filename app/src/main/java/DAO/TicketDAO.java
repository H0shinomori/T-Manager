package DAO;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;

import dbManager.FirebaseSingleton;
import modelo.Ticket;

public class TicketDAO {
    private DatabaseReference databaseReference;

    public TicketDAO() {
        databaseReference = FirebaseSingleton.getInstance().getDatabaseReference();
    }

    public void insertarTicket(String titulo, String estado, String prioridad, String descripcion, OnCompleteListener<Void> onCompleteListener) {
        String id = databaseReference.child("ticket").push().getKey();
        Ticket ticket = new Ticket(titulo, estado, prioridad, descripcion);

        databaseReference.child("ticket").child(id).setValue(ticket).addOnCompleteListener(onCompleteListener);
    }


}
