package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptador;
import android.danyk.modelo.Ticket;
import android.danyk.modelo.User;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Inicio extends Fragment {
    ImageButton botonPerfil;
    Button boton_cerrar_sesion;
    FirebaseAuth mAuth;
    TextView mostrarNombre, dayOfWeekTextView, dateTextView;
    List<Ticket> elementos;
    RecyclerView recyclerView;
    ListaAdaptador listaAdaptador;
    DatabaseReference databaseReference;
    List<String> idsTickets;
    List<Ticket> guardados;
    List<String> idsTicketsGuardados;
    UserDAO userDAO;
    String rolUsuario;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        botonPerfil = view.findViewById(R.id.miPerfil);
        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesionDrawer);
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
        dayOfWeekTextView = view.findViewById(R.id.day_of_week_textview);
        dateTextView = view.findViewById(R.id.date_textview);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());
        dayOfWeekTextView.setText(dayOfWeek.toUpperCase(Locale.getDefault()));
        dateTextView.setText(date);


        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                if (drawerLayout != null) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userDAO.getUserID()).child("idsGuardados");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsTicketsGuardados.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    String idTicket = dt.getValue(String.class);
                    idsTicketsGuardados.add(idTicket);
                }
                actualizarTicketsInicio();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            rolUsuario = bundle.getString("rolUsuario");
        }
        TextView nombreUsuarioDrawer = view.findViewById(R.id.nombreUsuarioDrawer);
        TextView emailDrawer = view.findViewById(R.id.emailDrawer);
        TextView rolUsuarioDrawer = view.findViewById(R.id.rolUsuarioDrawer);

        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getCurrentUser();
        if (currentUser != null) {
            String nombre = currentUser.getNombre();
            String correo = currentUser.getCorreo();
            String rol = rolUsuario;

            if (nombre != null) {
                mostrarNombre.setText(nombre);
                nombreUsuarioDrawer.setText(nombre);
            }
            if (correo != null) {
                emailDrawer.setText(correo);
            }
            if (rol != null) {
                rolUsuarioDrawer.setText(rol);
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
        DatabaseReference ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
        ticketsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elementos.clear();
                for (DataSnapshot dt : snapshot.getChildren()) {
                    Ticket ticket = dt.getValue(Ticket.class);
                    // Añade la condición para filtrar los tickets según el campo "finalizado"
                    if (!idsTicketsGuardados.contains(ticket.getIdTicket()) && !ticket.isFinalizado()) {
                        elementos.add(ticket);
                    } else if (ticket.isFinalizado()) {
                        // Elimina el ticket de la lista de inicio si está finalizado
                        eliminarTicket(ticket.getIdTicket());
                    }
                }
                listaAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de error, si es necesario
            }
        });
    }

    // Método para eliminar un ticket de la lista de inicio
    private void eliminarTicket(String idTicket) {
        for (int i = 0; i < elementos.size(); i++) {
            Ticket ticket = elementos.get(i);
            if (ticket.getIdTicket().equals(idTicket)) {
                elementos.remove(i);
                listaAdaptador.notifyItemRemoved(i);
                break;
            }
        }
    }
}