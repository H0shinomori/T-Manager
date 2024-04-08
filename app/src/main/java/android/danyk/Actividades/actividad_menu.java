package android.danyk.Actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.danyk.Fragmentos.Guardados;
import android.danyk.Fragmentos.Historial;
import android.danyk.Fragmentos.Inicio;
import android.danyk.Fragmentos.Notificaciones;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptador;
import android.danyk.modelo.Ticket;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class actividad_menu extends AppCompatActivity {

    private List<Ticket> ticketsGuardados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_menu);
        BottomNavigationView barraNavegacion = findViewById(R.id.barraVista);
        FloatingActionButton boton_ticket = findViewById(R.id.floatingActionButton);

        boton_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actividad_menu.this, actividad_ticket.class);
                overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                startActivity(intent);
            }
        });

        ticketsGuardados = new ArrayList<>();
        barraNavegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

                if (itemID == R.id.navInicio) {
                    cargarFragmento(new Inicio(), true);
                } else if (itemID == R.id.navGuardados) {
                    cargarFragmento(new Guardados(), false);
                } else if (itemID == R.id.navNotificaciones) {
                    cargarFragmento(new Notificaciones(), false);
                } else if (itemID == R.id.navHistorial) {
                    cargarFragmento(new Historial(), false);
                }
                return true;
            }
        });
        cargarFragmento(new Inicio(), true);
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
}
