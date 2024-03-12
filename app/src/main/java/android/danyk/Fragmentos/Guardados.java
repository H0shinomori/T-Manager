package android.danyk.Fragmentos;

import android.annotation.SuppressLint;
import android.danyk.R;
import android.danyk.Utilidades.ListaAdaptador;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class Guardados extends Fragment {
    RecyclerView recyclerView;
    ListaAdaptador adaptador;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardados, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        adaptador = new ListaAdaptador(new ArrayList<>(), this);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener los tickets guardados y actualizar el adaptador

        return view;
    }
}