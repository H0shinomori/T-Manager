package android.danyk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class actividad_ticket extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    TextView mostrarNombre, estado;
    TextInputEditText titulo, descripcion;
    RadioButton rb1, rb2, rb3;
    Button botonEnvio;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ticket);

        mostrarNombre = findViewById(R.id.creacion_ticket_usuario);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            String nombre = usuario.getDisplayName();

            if (nombre != null) {
                mostrarNombre.setText(nombre);
            }
        }

        estado = findViewById(R.id.creacion_ticket_estado);
        botonEnvio = findViewById(R.id.creacion_ticket_enviar);
        titulo = findViewById(R.id.creacion_ticket_titulo);
        descripcion = findViewById(R.id.creacion_ticket_descripcion);
        rb1 = findViewById(R.id.creacion_ticket_baja);
        rb2 = findViewById(R.id.creacion_ticket_media);
        rb3 = findViewById(R.id.creacion_ticket_alta);

        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Titulo, Estado, Descripcion, Prioridad;
                Titulo = String.valueOf(titulo.getText());
                Estado = estado.getText().toString();
                Descripcion = String.valueOf(descripcion.getText());
                if (rb1.isChecked()) {
                    Prioridad = "Baja";
                } else if (rb2.isChecked()) {
                    Prioridad = "Media";
                } else if (rb3.isChecked()) {
                    Prioridad = "Alta";
                } else {
                    Prioridad = "Baja";
                }


                Map<String, Object> ticketDatos = new HashMap<>();
                ticketDatos.put("titulo", Titulo);
                ticketDatos.put("estado", Estado);
                ticketDatos.put("descripcion", Descripcion);
                ticketDatos.put("prioridad", Prioridad);


                assert usuario != null;
                String userId = usuario.getUid();
                firestore.collection("ticket").document(userId).set(ticketDatos, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(actividad_ticket.this, "Ticket guardado exitosamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(actividad_ticket.this, Inicio.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(actividad_ticket.this, "Error al guardar el ticket", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}