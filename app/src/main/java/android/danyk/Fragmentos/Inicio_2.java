package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.modelo.Ticket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class Inicio_2 extends Fragment {
    private ImageButton boton_cerrar_sesion;
    private FirebaseAuth mAuth;
    TextView mostrarNombre, dayOfWeekTextView, dateTextView;
    private TextView contadorTicketPendiente;
    private TextView contadorTicketFinalizado;
    private DatabaseReference ticketsReference;
    private String userID;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_2, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesion);
        mostrarNombre = view.findViewById(R.id.nombre_usuario);
        mAuth = FirebaseAuth.getInstance();
        contadorTicketPendiente = view.findViewById(R.id.contadorTicketPendiente);
        contadorTicketFinalizado = view.findViewById(R.id.contadorTicketFinalizado);
        ticketsReference = FirebaseDatabase.getInstance().getReference("ticket");
        dayOfWeekTextView = view.findViewById(R.id.day_of_week_textview);
        dateTextView = view.findViewById(R.id.date_textview);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());

        dayOfWeekTextView.setText(dayOfWeek.toUpperCase(Locale.getDefault()));
        dateTextView.setText(date);

        UserDAO userDAO = new UserDAO();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (userDAO.checkCurrentUser() && usuario != null) {
            String nombre = userDAO.getUserNombre();
            userID = usuario.getUid();
            mostrarNombre.setText(nombre);

            boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDAO.cerrarSesion(getActivity());
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

                for (DataSnapshot dt : snapshot.getChildren()) {
                    Ticket ticket = dt.getValue(Ticket.class);
                    if (ticket != null) {
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
