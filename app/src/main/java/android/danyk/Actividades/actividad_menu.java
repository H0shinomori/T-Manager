package android.danyk.Actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.danyk.Fragmentos.Guardados;
import android.danyk.Fragmentos.Historial;
import android.danyk.Fragmentos.Inicio;
import android.danyk.Fragmentos.Inicio_2;
import android.danyk.Fragmentos.MisTickets;
import android.danyk.Fragmentos.Perfil;
import android.danyk.R;
import android.danyk.dbManager.UserDatabaseManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class actividad_menu extends AppCompatActivity {

    private String rolUsuario;
    private BottomNavigationView barraNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_menu);

        barraNavegacion = findViewById(R.id.barraVista);
        FloatingActionButton boton_ticket = findViewById(R.id.floatingActionButton);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, actividad_login.class));
            return;
        }
        UserDatabaseManager.crearUsuarioSiNoExiste(currentUser.getUid());
        obtenerRolUsuarioDesdeBaseDeDatos();

        boton_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actividad_menu.this, actividad_ticket.class);
                overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                startActivity(intent);
            }
        });
        barraNavegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.navInicio) {
                    if (rolUsuario != null) {
                        if (rolUsuario.equals("Tecnico")) {
                            cargarFragmento(new Inicio(), false);
                        } else if (rolUsuario.equals("Usuario")) {
                            cargarFragmento(new Inicio_2(), false);
                        }
                    }
                } else if (itemID == R.id.navGuardados) {
                    if (rolUsuario != null){
                        if (rolUsuario.equals("Tecnico")) {
                            cargarFragmento(new Guardados(), false);
                        } else if (rolUsuario.equals("Usuario")) {
                            // No hagas nada aquí para que el ítem "Guardados" no se procese para usuarios normales
                        }
                    }
                } else if (itemID == R.id.navMistickets) {
                    cargarFragmento(new MisTickets(), false);
                } else if (itemID == R.id.navHistorial) {
                    if (rolUsuario != null) {
                        if (rolUsuario.equals("Tecnico")) {
                            cargarFragmento(new Historial(), false);
                        } else if (rolUsuario.equals("Usuario")) {
                            Fragment perfilFragment = new Perfil();
                            Bundle bundle = new Bundle();
                            bundle.putString("rolUsuario", rolUsuario);
                            perfilFragment.setArguments(bundle);
                            cargarFragmento(perfilFragment, false);
                        }
                    }
                }
                return true;
            }
        });

    }

    private void obtenerRolUsuarioDesdeBaseDeDatos() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    rolUsuario = document.getString("rol");
                    cargarFragmentoSegunRol();
                    if (rolUsuario != null && rolUsuario.equals("Usuario")) {
                        eliminarIconoYTexto(R.id.navGuardados);
                        eliminarIconoYTexto(R.id.navHistorial);
                        ajustarEspaciosVacios();
                    }
                } else {
                    Toast.makeText(actividad_menu.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarFragmentoSegunRol() {
        if (rolUsuario != null) {
            Fragment fragment;
            if (rolUsuario.equals("Tecnico")) {
                fragment = new Inicio();
            } else if (rolUsuario.equals("Usuario")) {
                fragment = new Inicio_2();
            } else {
                fragment = new Fragment(); // Fragmento por defecto o manejo de error
            }
            Bundle bundle = new Bundle();
            bundle.putString("rolUsuario", rolUsuario);
            fragment.setArguments(bundle);
            cargarFragmento(fragment, true);
        }
    }


    private void cargarFragmento(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }



    // Método para eliminar un ícono y texto de un elemento del menú
    private void eliminarIconoYTexto(int itemId) {
        Menu menu = barraNavegacion.getMenu();
        MenuItem item = menu.findItem(itemId);
        item.setVisible(false); // Oculta el ítem del menú
    }

    private void ajustarEspaciosVacios() {
        // Calcula el tamaño de los espacios vacíos dependiendo del rol del usuario
        int espacioVacioIzquierda = getResources().getDimensionPixelSize(R.dimen.espacio_vacio_izquierda);
        int espacioVacioDerecha = getResources().getDimensionPixelSize(R.dimen.espacio_vacio_derecha);

        if (rolUsuario != null && rolUsuario.equals("Usuario")) {
            // Ajusta los espacios vacíos adicionales si es necesario
            espacioVacioIzquierda += getResources().getDimensionPixelSize(R.dimen.espacio_extra_vacio_izquierda_usuario);
            espacioVacioDerecha += getResources().getDimensionPixelSize(R.dimen.espacio_extra_vacio_derecha_usuario);
        }
        int margen = espacioVacioIzquierda + espacioVacioDerecha;
        // Ajusta el margen izquierdo del BottomNavigationView para centrar el ícono del inicio
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) barraNavegacion.getLayoutParams();
        layoutParams.leftMargin = margen;
        // Ajusta el margen derecho del BottomNavigationView para agregar espacio después del ícono del inicio
        barraNavegacion.setLayoutParams(layoutParams);
    }

}
