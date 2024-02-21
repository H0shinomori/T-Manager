package android.danyk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import modelo.Ticket;

public class actividad_menu extends AppCompatActivity {
    FloatingActionButton boton_ticket;
    private BottomNavigationView barraNavegacion;
    private FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_menu);
        frameLayout = findViewById(R.id.frameLayout);
        barraNavegacion = findViewById(R.id.barraVista);
        boton_ticket = findViewById(R.id.floatingActionButton);

        boton_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actividad_menu.this, actividad_ticket.class);
                startActivity(intent);
            }
        });

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
