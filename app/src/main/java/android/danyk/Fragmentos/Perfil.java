package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.modelo.User;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends Fragment {

    TextView nombreUsuarioTextView, emailTextView, rolUsuarioTextView;
    Button boton_cerrar_sesion;
    String rolUsuario;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);

        nombreUsuarioTextView = view.findViewById(R.id.nombreUsuarioPerfil);
        emailTextView = view.findViewById(R.id.emailPerfil);
        rolUsuarioTextView = view.findViewById(R.id.rolUsuarioPerfil);
        boton_cerrar_sesion = view.findViewById(R.id.cerrar_sesionPerfil);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String rolUsuario = bundle.getString("rolUsuario");
            if (rolUsuario != null) {
                rolUsuarioTextView.setText(rolUsuario);
            }
        }


        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getCurrentUser();
        if (currentUser != null) {
            String nombre = currentUser.getNombre();
            String correo = currentUser.getCorreo();

            if (nombre != null) {
                nombreUsuarioTextView.setText(nombre);
            }
            if (correo != null) {
                emailTextView.setText(correo);
            }

            boton_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDAO.cerrarSesion(getActivity());
                }
            });
        }

        return view;
    }
}
