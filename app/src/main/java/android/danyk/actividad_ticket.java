package android.danyk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class actividad_ticket extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView mostrarNombre, estado;
    TextInputEditText titulo, descripcion;
    RadioGroup prioridad;
    Button botonEnvio;
    Ticket ticket;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ticket);

        mostrarNombre = findViewById(R.id.creacion_ticket_usuario);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        estado = findViewById(R.id.creacion_ticket_estado);
        botonEnvio = findViewById(R.id.creacion_ticket_enviar);
        titulo = findViewById(R.id.creacion_ticket_titulo);
        descripcion = findViewById(R.id.creacion_ticket_descripcion);
        prioridad = findViewById(R.id.creacion_ticket_prioridad);

        mostarNombre();
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarDatos();
            }
        });
    }

    private void insertarDatos() {
        String Titulo, Estado, Prioridad;
        Titulo = String.valueOf(titulo.getText());
        Estado = estado.getText().toString();

        int radioButtonId = prioridad.getCheckedRadioButtonId();

        if (radioButtonId != -1) {
            RadioButton radioButton = findViewById(radioButtonId);
            Prioridad = radioButton.getText().toString();
            String id = databaseReference.push().getKey();
            ticket = new Ticket(Titulo, Estado, Prioridad);
            databaseReference.child("ticket").child(id).setValue(ticket).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(actividad_ticket.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(actividad_ticket.this, "Seleccione una prioridad", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostarNombre(){
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            String nombre = usuario.getDisplayName();
            if (nombre != null) {
                mostrarNombre.setText(nombre);
            }
        }
    }
}
