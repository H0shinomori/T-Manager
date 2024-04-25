package android.danyk.Actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.danyk.DAO.TicketDAO;
import android.danyk.R;
import android.danyk.modelo.Ticket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class actividad_editarTicket extends AppCompatActivity {
    String usuarioActualId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button botonCancelar, botonConfirmar;
    DatabaseReference ticketsEditadosRef;
    DatabaseReference ticketsRef;
    TextView tituloPreview, estadoPreview, prioridadPreview, descripcionPreview, notasPreview;
    LinearLayout layoutVistaPreviaImagen;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_editar_ticket);

        layoutVistaPreviaImagen = findViewById(R.id.layout_vistaPreviaImagen);
        botonCancelar = findViewById(R.id.boton_cancelar);
        botonConfirmar = findViewById(R.id.boton_confirmar);
        tituloPreview = findViewById(R.id.tituloTextViewPreview);
        estadoPreview = findViewById(R.id.estadoTextViewPreview);
        prioridadPreview = findViewById(R.id.prioridadTextViewPreview);
        descripcionPreview = findViewById(R.id.descripcionTextViewPreview);
        notasPreview = findViewById(R.id.notasTextViewPreview);

        Ticket ticket = getIntent().getParcelableExtra("ticket");
        if (ticket != null) {
            tituloPreview.setText(ticket.getTitulo());
            estadoPreview.setText(ticket.getEstado());
            prioridadPreview.setText(ticket.getPrioridad());
            descripcionPreview.setText(ticket.getDescripcion());
        }
        layoutVistaPreviaImagen.removeAllViews();

        if (ticket != null && ticket.getImageUris() != null && !ticket.getImageUris().isEmpty()) {
            for (String imageUrl : ticket.getImageUris()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        dipToPixels(this, 250),
                        dipToPixels(this, 400)
                );
                layoutParams.setMargins(10, 0, 10, 0);
                imageView.setLayoutParams(layoutParams);
                Glide.with(this).load(imageUrl).into(imageView);
                layoutVistaPreviaImagen.addView(imageView);
            }
        }

        ticketsRef = FirebaseDatabase.getInstance().getReference().child("ticket"); // Agregado
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actividad_editarTicket.this, actividad_menu.class);
                startActivity(intent);
            }
        });
        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert ticket != null;
                insertarDatos(ticket);
            }
        });
        DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("ticket");

        ticketRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String idTicket = dataSnapshot.getKey();
                    assert ticket != null;
                    if (ticket.getIdTicket().equals(idTicket)) {
                        dataSnapshot.getRef().removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Ticket eliminado con éxito
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejar error
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
            }
        });
    }

    private int dipToPixels(actividad_editarTicket actividadEditarTicket, int dpValue) {
        float scale = actividadEditarTicket.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void insertarDatos(Ticket ticket) {
        String Titulo, Estado, Prioridad, Descripcion, Notas;
        Titulo = String.valueOf(tituloPreview.getText());
        Estado = "Finalizado";
        Descripcion = String.valueOf(descripcionPreview.getText());
        Notas = String.valueOf(notasPreview.getText());
        Prioridad = prioridadPreview.getText().toString();

        Map<String, Object> ticketValues = new HashMap<>();
        ticketValues.put("titulo", Titulo);
        ticketValues.put("estado", Estado);
        ticketValues.put("prioridad", Prioridad);
        ticketValues.put("descripcion", Descripcion);
        ticketValues.put("notas_añadidas", Notas);
        ticketValues.put("idTicket", ticket.getIdTicket());
        ticketValues.put("resuelto_por", usuarioActualId);
        ticketValues.put("finalizado", true);

        if (ticket.getImageUris() != null && !ticket.getImageUris().isEmpty()) {
            Map<String, String> imagenes = new HashMap<>();
            for (int i = 0; i < ticket.getImageUris().size(); i++) {
                imagenes.put("imagenUrl" + i, ticket.getImageUris().get(i));
            }
            ticketValues.put("imagenes", imagenes);
        }

        String ticketId = ticket.getIdTicket();
        DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("ticket").child(ticketId);

        // Actualiza los valores del ticket en el nodo original "ticket"
        ticketRef.updateChildren(ticketValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Aquí puedes realizar cualquier acción adicional después de actualizar los datos
                        // Luego, continúa con el proceso de mover el ticket a "tickets_finalizados"
                        moverTicketAFinalizados(ticketId, ticketValues);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejo de errores
                    }
                });
    }

    private void moverTicketAFinalizados(String ticketId, Map<String, Object> ticketValues) {
        // Agrega el ticket actualizado a "tickets_finalizados"
        DatabaseReference ticketsFinalizadosRef = FirebaseDatabase.getInstance().getReference("ticket").push();
        ticketsFinalizadosRef.setValue(ticketValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Elimina el ticket original del nodo "ticket"
                        DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("ticket").child(ticketId);
                        ticketRef.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Aquí puedes agregar cualquier acción adicional después de eliminar el ticket original
                                        Intent intent = new Intent(actividad_editarTicket.this, actividad_menu.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejo de errores
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejo de errores
                    }
                });
    }



}
