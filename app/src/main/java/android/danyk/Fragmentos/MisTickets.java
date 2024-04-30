package android.danyk.Fragmentos;

import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptadorMisTickets;
import android.danyk.Utilidades.ListaAdaptadorTicketsGuardados;
import android.danyk.modelo.Ticket;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MisTickets extends Fragment {
    RecyclerView recyclerView;
    List<Ticket> elementos;
    ListaAdaptadorMisTickets listaAdaptador;
    DatabaseReference databaseReference;
    List<String> idsMisTickets;
    UserDAO userDAO;
    Spinner spinnerFilter;
    String selectedFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_tickets, container, false);
        recyclerView = view.findViewById(R.id.recycleViewMisTickets);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        elementos = new ArrayList<>();
        idsMisTickets = new ArrayList<>();

        listaAdaptador = new ListaAdaptadorMisTickets(elementos, requireContext(), idsMisTickets);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(listaAdaptador);
        userDAO = new UserDAO();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userDAO.getUserID()).child("idsCreados");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsMisTickets.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getValue(String.class);
                    idsMisTickets.add(idTicket);
                }
                // Actualizar la lista de tickets creados
                actualizarTicketsCreados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.filters, R.layout.spinner_dropdown);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerFilter = view.findViewById(R.id.spinnerFilter);
        spinnerFilter.setBackgroundResource(R.drawable.spinner_custom);
        spinnerFilter.setAdapter(adapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedFilter = getResources().getStringArray(R.array.filters)[position];
                actualizarTicketsCreados();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hacemos nada aquí
            }
        });

        return view;
    }

    private void actualizarTicketsCreados() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userDAO.getUserID()).child("idsCreados");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> idsMisTickets = new ArrayList<>();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getValue(String.class);
                    idsMisTickets.add(idTicket);
                }
                DatabaseReference ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
                ticketsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        elementos.clear();
                        for (DataSnapshot dt : snapshot.getChildren()) {
                            Ticket ticket = dt.getValue(Ticket.class);
                            if (idsMisTickets.contains(ticket.getIdTicket()) && cumpleFiltro(ticket)) {
                                if (ticket.getEstado().equals("Finalizado")) {
                                    // Si el ticket está finalizado, lo agregamos a la lista sin cambios
                                    elementos.add(ticket);
                                } else {
                                    // Si el ticket está pendiente, verificamos si hay otro ticket finalizado con el mismo ID
                                    boolean encontrado = false;
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Ticket otroTicket = dataSnapshot.getValue(Ticket.class);
                                        if (otroTicket.getIdTicket().equals(ticket.getIdTicket()) && otroTicket.getEstado().equals("Finalizado")) {
                                            encontrado = true;
                                            break;
                                        }
                                    }
                                    // Si no se encontró un ticket finalizado con el mismo ID, agregamos el ticket pendiente
                                    if (!encontrado) {
                                        elementos.add(ticket);
                                    }
                                }
                            }
                        }
                        listaAdaptador.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }




    private boolean cumpleFiltro(Ticket ticket) {
        if (selectedFilter.equals("Pendientes")) {
            return ticket.getEstado().equals("Pendiente");
        } else if (selectedFilter.equals("Finalizados")) {
            return ticket.getEstado().equals("Finalizado");
        }
        return false;
    }
}
