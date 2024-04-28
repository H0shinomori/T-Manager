package android.danyk.Fragmentos;

import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptadorTicketsGuardados;
import android.danyk.modelo.Ticket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Guardados extends Fragment {
    RecyclerView recyclerView;
    List<Ticket> elementos;
    ListaAdaptadorTicketsGuardados listaAdaptador;
    DatabaseReference databaseReference;
    List<String> idsTicketsGuardados;
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardados, container, false);
        recyclerView = view.findViewById(R.id.recycleViewGuardados);

        elementos = new ArrayList<>();
        idsTicketsGuardados = new ArrayList<>();

        listaAdaptador = new ListaAdaptadorTicketsGuardados(elementos, requireContext(), idsTicketsGuardados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(listaAdaptador);
        userDAO = new UserDAO();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userDAO.getUserID()).child("idsGuardados");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsTicketsGuardados.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getValue(String.class);
                    idsTicketsGuardados.add(idTicket);
                }
                actualizarTicketsGuardados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
    private void actualizarTicketsGuardados() {
        DatabaseReference ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
        ticketsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elementos.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    Ticket ticket = dt.getValue(Ticket.class);
                    if (idsTicketsGuardados.contains(ticket.getIdTicket())) {
                        elementos.add(ticket);
                    }
                }
                listaAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
