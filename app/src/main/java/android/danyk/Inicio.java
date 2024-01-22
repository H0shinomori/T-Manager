package android.danyk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends Fragment {
    Button boton_cerrar_sesion;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    TextView mostrarNombre;
    List<Ticket> elementos;
    RecyclerView recyclerView;
    ListaAdaptador listaAdaptador;
    CollectionReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesion);
        mostrarNombre = view.findViewById(R.id.nombre_usuario);
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recycleView);
        firestore = FirebaseFirestore.getInstance();
        databaseReference = firestore.collection("ticket");
        elementos = new ArrayList<>();
        listaAdaptador = new ListaAdaptador(elementos, this);
        recyclerView.setAdapter(listaAdaptador);

        firestore.collection("ticket").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){

                    return;
                }
                elementos.clear();
                for (QueryDocumentSnapshot document : value) {
                    Ticket ticket = document.toObject(Ticket.class);
                    elementos.add(ticket);
                }
                listaAdaptador.notifyDataSetChanged();
            }
        });


        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            String nombre = usuario.getDisplayName();

            if (nombre != null) {
                mostrarNombre.setText(nombre);
            }
            boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), actividad_login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
        return view;
    }
}

