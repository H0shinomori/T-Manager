package android.danyk.Fragmentos;

import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptadorHistorial;
import android.danyk.modelo.Ticket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Historial extends Fragment {
    RecyclerView recyclerView;
    List<Ticket> elementos;
    ListaAdaptadorHistorial listaAdaptador;
    DatabaseReference databaseReference;
    List<String> idsTicketsHistorial;
    UserDAO userDAO;
    SearchView searchViewHistorial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        recyclerView = view.findViewById(R.id.recycleViewHistorial);

        elementos = new ArrayList<>();
        idsTicketsHistorial = new ArrayList<>();

        listaAdaptador = new ListaAdaptadorHistorial(elementos, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(listaAdaptador);

        userDAO = new UserDAO();
        databaseReference = FirebaseDatabase.getInstance().getReference("ticket");

        searchViewHistorial = view.findViewById(R.id.searchViewHistorial);
        searchViewHistorial.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaAdaptador.filtrarPorTitulo(newText);
                return true;
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elementos.clear();
                idsTicketsHistorial.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getKey();
                    idsTicketsHistorial.add(idTicket);
                    Ticket ticket = dt.getValue(Ticket.class);
                    if (ticket != null && ticket.isFinalizado()) {
                        elementos.add(ticket);
                    }
                }
                String searchText = searchViewHistorial.getQuery().toString();
                listaAdaptador.filtrarPorTitulo(searchText);
                listaAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }
}
