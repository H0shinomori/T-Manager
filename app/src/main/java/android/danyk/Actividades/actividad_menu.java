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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class actividad_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_menu);

        BottomNavigationView barraNavegacion = findViewById(R.id.barraVista);
        FloatingActionButton boton_ticket = findViewById(R.id.floatingActionButton);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, actividad_login.class));
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("DominioPrefs", Context.MODE_PRIVATE);
        UserDatabaseManager.crearUsuarioSiNoExiste(currentUser.getUid());

        String dominio = sharedPreferences.getString("dominio", "");
        dominio = dominio.trim();
        if (dominio.equals("inf.ceac.com")) {
            cargarFragmento(new Inicio(), true);
        } else if (dominio.equals("ceac.com")) {
            cargarFragmento(new Inicio_2(), true);
        }



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
                    String dominio = sharedPreferences.getString("dominio", "");
                    dominio = dominio.trim();
                    if (dominio.equals("inf.ceac.com")) {
                        cargarFragmento(new Inicio(), false);
                    } else if (dominio.equals("ceac.com")) {
                        cargarFragmento(new Inicio_2(), false);
                    }
                } else if (itemID == R.id.navGuardados) {
                    cargarFragmento(new Guardados(), false);
                } else if (itemID == R.id.navMistickets) {
                    cargarFragmento(new MisTickets(), false);
                } else if (itemID == R.id.navHistorial) {
                    String dominio = sharedPreferences.getString("dominio", "");
                    dominio = dominio.trim();
                    if (dominio.equals("inf.ceac.com")) {
                        cargarFragmento(new Historial(), false);
                    } else if (dominio.equals("ceac.com")) {
                        cargarFragmento(new Perfil(), false);
                    }
                }
                return true;
            }
        });
        if (Objects.requireNonNull(currentUser.getEmail()).endsWith("@ceac.com")) {
            cambiarIconoPerfilEnHistorial(barraNavegacion.getMenu());
            cambiarTextoPerfilEnHistorial(barraNavegacion.getMenu());
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


    private void cambiarIconoPerfilEnHistorial(Menu menu) {
        MenuItem historialItem = menu.findItem(R.id.navHistorial);
        historialItem.setIcon(R.drawable.ic_perfil2);
    }
    private void cambiarTextoPerfilEnHistorial(Menu menu) {
        MenuItem historialItem = menu.findItem(R.id.navHistorial);
        historialItem.setTitle("Mi Perfil");
    }
}