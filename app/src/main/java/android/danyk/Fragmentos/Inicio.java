package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.Utilidades.ListaAdaptador;
import android.danyk.R;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.danyk.DAO.UserDAO;
import android.danyk.modelo.Ticket;

public class Inicio extends Fragment {
    ImageButton boton_cerrar_sesion;
    FirebaseAuth mAuth;
    TextView mostrarNombre;
    List<Ticket> elementos;
    RecyclerView recyclerView;
    ListaAdaptador listaAdaptador;
    DatabaseReference databaseReference;
    List<String> idsTickets;
    List<Ticket> guardados;
    List<String> idsTicketsGuardados;
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesion);
        mostrarNombre = view.findViewById(R.id.nombre_usuario);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        databaseReference = FirebaseDatabase.getInstance().getReference("ticket");
        elementos = new ArrayList<>();
        idsTickets = new ArrayList<>();
        idsTicketsGuardados = new ArrayList<>();
        userDAO = new UserDAO();
        listaAdaptador = new ListaAdaptador(elementos, this, idsTickets, guardados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listaAdaptador);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userDAO.getUserID()).child("idsGuardados");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsTicketsGuardados.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getValue(String.class);
                    idsTicketsGuardados.add(idTicket);
                }
                // Actualizar la lista de tickets guardados
                actualizarTicketsInicio();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        UserDAO userDAO = new UserDAO();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (userDAO.checkCurrentUser() && usuario != null) {
            String nombre = userDAO.getUserNombre();

            if (nombre != null) {
                mostrarNombre.setText(nombre);
            }
            boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDAO.cerrarSesion(getActivity());
                }
            });
        }
        return view;
    }

    private void actualizarTicketsInicio() {
        // Obtener todos los tickets de la base de datos
        DatabaseReference ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
        ticketsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elementos.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    Ticket ticket = dt.getValue(Ticket.class);
                    // Filtrar los tickets para excluir aquellos cuyos IDs están en la lista de tickets guardados
                    if (!idsTicketsGuardados.contains(ticket.getIdTicket())) {
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