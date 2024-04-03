package android.danyk.Fragmentos;

import android.danyk.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


public class Guardados extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardados, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycleViewGuardados);

        return view;
    }

}
