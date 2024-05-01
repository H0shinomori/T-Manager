package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.modelo.Ticket;
import android.danyk.modelo.User;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.WindowManager;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Inicio_2 extends Fragment {
    private ImageButton boton_perfil;
    private FirebaseAuth mAuth;
    TextView mostrarNombre, dayOfWeekTextView, dateTextView;
    private TextView contadorTicketPendiente;
    private TextView contadorTicketFinalizado;
    private DatabaseReference ticketsReference;
    private String userID;
    Button boton_cerrar_sesion;
    LinearLayout drawerContainer;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_2, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boton_perfil = view.findViewById(R.id.miPerfil);
        mostrarNombre = view.findViewById(R.id.nombre_usuario);
        mAuth = FirebaseAuth.getInstance();
        contadorTicketPendiente = view.findViewById(R.id.contadorTicketPendiente);
        contadorTicketFinalizado = view.findViewById(R.id.contadorTicketFinalizado);
        ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
        dayOfWeekTextView = view.findViewById(R.id.day_of_week_textview);
        dateTextView = view.findViewById(R.id.date_textview);
        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesionDrawer);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());
        dayOfWeekTextView.setText(dayOfWeek.toUpperCase(Locale.getDefault()));
        dateTextView.setText(date);
        drawerContainer = view.findViewById(R.id.drawer_content);
        drawerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ignora los clicks en el drawer
            }
        });

        TextView nombreUsuarioDrawer = view.findViewById(R.id.nombreUsuarioDrawer);
        TextView emailDrawer = view.findViewById(R.id.emailDrawer);

        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getCurrentUser();
        if (currentUser != null) {
            String nombre = currentUser.getNombre();
            String correo = currentUser.getCorreo();


            if (nombre != null) {
                mostrarNombre.setText(nombre);
                nombreUsuarioDrawer.setText(nombre);
            }
            if (correo != null) {
                emailDrawer.setText(correo);
            }

            boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDAO.cerrarSesion(getActivity());
                }
            });


            boton_perfil.setOnClickListener(new View.OnClickListener() {
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

            // Contador de tickets pendientes y finalizados por usuario
            actualizarContadoresTicketsPorUsuario();
        }

        return view;
    }

    private void actualizarContadoresTicketsPorUsuario() {
        ticketsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countPendiente = 0;
                int countFinalizado = 0;
                UserDAO userDAO = new UserDAO();

                for (DataSnapshot dt : snapshot.getChildren()) {
                    Ticket ticket = dt.getValue(Ticket.class);
                    if (ticket != null && Objects.equals(ticket.getCreadoPor(), userDAO.getCurrentUser().getCorreo())) {
                        if (ticket.getEstado().equals("Pendiente")) {
                            countPendiente++;
                        } else if (ticket.getEstado().equals("Finalizado")) {
                            countFinalizado++;
                        }
                    }
                }

                // Actualizar los contadores
                contadorTicketPendiente.setText(String.valueOf(countPendiente));
                contadorTicketFinalizado.setText(String.valueOf(countFinalizado));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de errores
            }
        });
    }
}
