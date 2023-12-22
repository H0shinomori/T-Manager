package android.danyk;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class actividad_registro extends AppCompatActivity {
    TextInputEditText nombre, apellido, email, contra;
    Button boton_registro;
    FirebaseAuth mAuth;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_registro);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual != null) {
            Intent intent = new Intent(getApplicationContext(), actividad_login.class);
            startActivity(intent);
            finish();
            return;
        }

        nombre = findViewById(R.id.nombre_registro);
        apellido = findViewById(R.id.apellido_registro);
        email = findViewById(R.id.email_registro);
        contra = findViewById(R.id.contra_registro);
        boton_registro = findViewById(R.id.boton_registroCompleto);


        boton_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, password;
                mail = String.valueOf(email.getText());
                password = String.valueOf(contra.getText());

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(actividad_registro.this, "Introduzca el correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(actividad_registro.this, "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(actividad_registro.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(actividad_registro.this, actividad_menu.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(actividad_registro.this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

