package android.danyk.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.danyk.R;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.danyk.DAO.UserDAO;


public class actividad_login extends AppCompatActivity {

    TextView correo, contraseña;
    UserDAO userDAO = new UserDAO();
    SharedPreferences sharedPreferences;

    @Override
    public void onStart() {
        super.onStart();
        if(userDAO.checkCurrentUser()){
            Intent intent = new Intent(getApplicationContext(), actividad_menu.class);
            overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_login);

        sharedPreferences = getSharedPreferences("DominioPrefs", Context.MODE_PRIVATE);
        correo = findViewById(R.id.email_login);
        contraseña = findViewById(R.id.contra_login);
        Button botonLogin = findViewById(R.id.boton_inicioSesion);

        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, password;
                mail = String.valueOf(correo.getText());
                password = String.valueOf(contraseña.getText());

                if (sharedPreferences != null) {
                    String[] parts = mail.split("@");
                    if (parts.length == 2) {
                        String domain = parts[1];
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("dominio", domain);
                        editor.apply();
                    }
                }
                userDAO.loginUser(actividad_login.this, mail, password);
            }
        });


        TextView registro = findViewById(R.id.login_registrate);
        String texto = "Registrate";
        SpannableString spannableString = new SpannableString(texto);
        int start = 0;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(actividad_login.this, actividad_registro.class);
                overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan,start,start + "Registrate".length(), 0);
        registro.setText(spannableString);
        registro.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
