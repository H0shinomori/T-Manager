package android.danyk.Actividades;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.danyk.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class actividad_registro extends AppCompatActivity {
    TextInputEditText nombre, apellido, email, contra;
    RadioButton rolUsuario, rolTecnico;
    Button boton_registro;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    TextInputEditText tecnicoContra;

    private static final String CONTRASEÑA_TECNICO = "tecnico";

    private boolean contraseñaCorrecta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_registro);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual != null) {
            Intent intent = new Intent(getApplicationContext(), actividad_login.class);
            overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
            startActivity(intent);
            finish();
            return;
        }

        tecnicoContra = findViewById(R.id.tecnico_contra);
        nombre = findViewById(R.id.nombre_registro);
        apellido = findViewById(R.id.apellido_registro);
        email = findViewById(R.id.email_registro);
        contra = findViewById(R.id.contra_registro);
        boton_registro = findViewById(R.id.boton_registroCompleto);
        rolTecnico = findViewById(R.id.rol_tecnico);
        rolUsuario = findViewById(R.id.rol_usuario);

        RadioGroup rolRadioGroup = findViewById(R.id.rol_radio_group);

        rolRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rol_tecnico) {
                    tecnicoContra.setVisibility(View.VISIBLE);
                } else {
                    tecnicoContra.setVisibility(View.INVISIBLE);
                }
            }
        });

        tecnicoContra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(CONTRASEÑA_TECNICO)) {
                    contraseñaCorrecta = true;
                } else {
                    contraseñaCorrecta = false;
                }
            }
        });

        boton_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, password, name, lastName;
                mail = String.valueOf(email.getText());
                password = String.valueOf(contra.getText());
                name = String.valueOf(nombre.getText());
                lastName = String.valueOf(apellido.getText());

                if (tecnicoContra.getVisibility() == View.VISIBLE && !contraseñaCorrecta) {
                    Toast.makeText(actividad_registro.this, "Por favor, ingrese la contraseña del técnico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName)) {
                    Toast.makeText(actividad_registro.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String rol;
                if (rolTecnico.isChecked()) {
                    rol = "Tecnico";
                } else if (rolUsuario.isChecked()) {
                    rol = "Usuario";
                } else {
                    Toast.makeText(actividad_registro.this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(actividad_registro.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                            FirebaseUser usuario = mAuth.getCurrentUser();
                            UserProfileChangeRequest actualizarPerfil = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                            usuario.updateProfile(actualizarPerfil).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> profileTask) {
                                    if (profileTask.isSuccessful()) {
                                        String userId = usuario.getUid();
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("nombre", name);
                                        userData.put("apellido", lastName);
                                        userData.put("email", mail);
                                        userData.put("contra", password);
                                        userData.put("rol", rol);

                                        firestore.collection("usuarios").document(userId).set(userData);
                                        Intent intent = new Intent(actividad_registro.this, actividad_menu.class);
                                        overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                                        intent.putExtra("ROL_USUARIO", rol);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(actividad_registro.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(actividad_registro.this, "Usuario y/o contraseña no válidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
