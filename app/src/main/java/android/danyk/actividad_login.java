package android.danyk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class actividad_login extends AppCompatActivity {

    TextView correo, contraseña;
    private Button botonLogin;
    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if(usuarioActual != null){
            Intent intent = new Intent(getApplicationContext(), actividad_menu.class);
            startActivity(intent);
            finish();
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_login);

        correo = findViewById(R.id.email_login);
        contraseña = findViewById(R.id.contra_login);
        mAuth = FirebaseAuth.getInstance();
        botonLogin = findViewById(R.id.boton_inicioSesion);


        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, password;
                mail = String.valueOf(correo.getText());
                password = String.valueOf(contraseña.getText());

                if(TextUtils.isEmpty(mail)){
                    Toast.makeText(actividad_login.this, "Introduzca el correo electronico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(actividad_login.this, "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(actividad_login.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intento = new Intent(getApplicationContext(), actividad_menu.class);
                                    startActivity(intento);
                                    finish();
                                }else{
                                    Toast.makeText(actividad_login.this,"Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        TextView registro = findViewById(R.id.login_registrate);
        String texto = "Registrate";
        SpannableString spannableString = new SpannableString(texto);
        int start = texto.indexOf("Registrate");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(actividad_login.this, actividad_registro.class);
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan,start,start + "Registrate".length(), 0);
        registro.setText(spannableString);
        registro.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
