package android.danyk.DAO;

import android.content.Context;
import android.content.Intent;
import android.danyk.Actividades.actividad_login;
import android.danyk.Actividades.actividad_menu;
import android.danyk.modelo.User;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.danyk.dbManager.FirebaseSingleton;

public class UserDAO {
    private FirebaseSingleton firebaseManager;
    private FirebaseAuth mAuth;

    public UserDAO() {
        firebaseManager = FirebaseSingleton.getInstance();
        mAuth = FirebaseSingleton.getInstance().getFirebaseAuth();
    }

    public String getUserNombre() {
        FirebaseUser firebaseUser = firebaseManager.getCurrentUser();
        if (firebaseUser != null) {
            String nombre = firebaseUser.getDisplayName();
            return nombre;

        }
        return null;
    }
    public String getUserID(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        return userId;
    }

    public Boolean checkCurrentUser(){
        if (mAuth.getCurrentUser() != null){
            return true;
        }
        else{
            return false;
        }
    }
    public User getCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String nombre = firebaseUser.getDisplayName();
            String correo = firebaseUser.getEmail();
            return new User(nombre, null, correo, null, null);
        }
        return null;
    }

    public void loginUser(Context context, String mail, String password){
        if(TextUtils.isEmpty(mail)){
            Toast.makeText(context, "Introduzca el correo electronico", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(context, "Introduzca la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(mail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                            Intent intento = new Intent(context.getApplicationContext(), actividad_menu.class);
                            context.startActivity(intento);
                        }else{
                            Toast.makeText(context,"Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cerrarSesion(Context context) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(context, actividad_login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


}
