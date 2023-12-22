package android.danyk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.danyk.databinding.ActividadMenuBinding;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class actividad_menu extends AppCompatActivity {
    FirebaseAuth auth;
    Button boton_cerrar_sesion;
    private BottomNavigationView barraNavegacion;
    private FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_menu);
        frameLayout = findViewById(R.id.frameLayout);
        barraNavegacion = findViewById(R.id.barraVista);

        auth = FirebaseAuth.getInstance();
        boton_cerrar_sesion = findViewById(R.id.cerrar_sesion);


        boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), actividad_login.class);
                startActivity(intent);
                finishAffinity();
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
        FragmentManager fragmento = getSupportFragmentManager();
        FragmentTransaction fragmentoTrans = fragmento.beginTransaction();
        if (isAppInitialized) {
            fragmentoTrans.add(R.id.frameLayout, fragment);
        } else {
            fragmentoTrans.replace(R.id.frameLayout, fragment);
        }
        fragmentoTrans.replace(R.id.frameLayout, fragment);
        fragmentoTrans.commit();
    }
}