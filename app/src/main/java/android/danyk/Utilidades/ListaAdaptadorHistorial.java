package android.danyk.Utilidades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.danyk.Actividades.actividad_editarTicket;
import android.danyk.DAO.TicketDAO;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.modelo.Ticket;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaAdaptadorHistorial extends RecyclerView.Adapter<ListaAdaptadorHistorial.ViewHolder> {
    private List<Ticket> datos;
    private List<Ticket> datosFiltrados;
    private final LayoutInflater inflater;
    private final Context context;



    public ListaAdaptadorHistorial(List<Ticket> itemList, Fragment context) {
        this.inflater = LayoutInflater.from(context.getContext());
        this.context = context.getContext();
        this.datos = itemList;
        this.datosFiltrados = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.ticket_recycle_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Ticket tickets = datosFiltrados.get(position);

        holder.titulo.setText(tickets.getTitulo());
        holder.estado.setText(tickets.getEstado());
        holder.prioridad.setText(tickets.getPrioridad());

        if (tickets.getPrioridad().equals("Alta")) {
            int color = ContextCompat.getColor(context, R.color.color_prioridad_alta);
            holder.prioridad.setTextColor(Integer.parseInt(String.valueOf(color)));
        } else if (tickets.getPrioridad().equals("Media")){
            int color = ContextCompat.getColor(context, R.color.color_prioridad_media);
            holder.prioridad.setTextColor(Integer.parseInt(String.valueOf(color)));
        }else if (tickets.getPrioridad().equals("Baja")) {
            int color = ContextCompat.getColor(context, R.color.color_prioridad_baja);
            holder.prioridad.setTextColor(Integer.parseInt(String.valueOf(color)));
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            @Override
            public void onClick(View v) {
                View dialogView = inflater.inflate(R.layout.ticket_preview2, null);
                TextView tituloPreview = dialogView.findViewById(R.id.tituloTextViewPreview);
                TextView estadoPreview = dialogView.findViewById(R.id.estadoTextViewPreview);
                TextView prioridadPreview = dialogView.findViewById(R.id.prioridadTextViewPreview);
                TextView descripcionPreview = dialogView.findViewById(R.id.descripcionTextViewPreview);
                LinearLayout layoutVistaPreviaImagen = dialogView.findViewById(R.id.layout_vistaPreviaImagen);
                TextView notasPreview = dialogView.findViewById(R.id.notasTextViewPreview);
                TextView hechoPorPreview = dialogView.findViewById(R.id.hechoPorTextViewPreview);

                tituloPreview.setText(tickets.getTitulo());
                estadoPreview.setText(tickets.getEstado());
                prioridadPreview.setText(tickets.getPrioridad());
                descripcionPreview.setText(tickets.getDescripcion());
                notasPreview.setText(tickets.getNotas());
                hechoPorPreview.setText(tickets.getHechoPor());

                layoutVistaPreviaImagen.removeAllViews();

                if (tickets.getImageUris() != null && !tickets.getImageUris().isEmpty()) {
                    for (String imageUrl : tickets.getImageUris()) {
                        ImageView imageView = new ImageView(context);
                        assert context != null;
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                dipToPixels(context, 250),
                                dipToPixels(context, 400)
                        );
                        layoutParams.setMargins(10, 0, 10, 0);
                        imageView.setLayoutParams(layoutParams);
                        Glide.with(context).load(imageUrl).into(imageView);
                        layoutVistaPreviaImagen.addView(imageView);
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog dialog = builder.create();
                assert context != null;
                Drawable background = ContextCompat.getDrawable(context, R.drawable.redondear_bordes);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);
                dialog.setView(dialogView);
                dialog.show();
            }
        });

    }

    public void filtrarPorTitulo(String texto) {
        datosFiltrados.clear();
        if (!TextUtils.isEmpty(texto)) {
            String patron = texto.toLowerCase().trim();
            for (Ticket ticket : datos) {
                if (ticket.getTitulo().toLowerCase().contains(patron)) {
                    datosFiltrados.add(ticket);
                }
            }
        } else {
            datosFiltrados.addAll(datos);
        }
        notifyDataSetChanged();
    }

    private int dipToPixels(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return datosFiltrados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado, prioridad;
        CardView cardView;
        ImageView iconoEstado;
        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.ticket_titulo);
            estado = itemView.findViewById(R.id.ticket_estado);
            prioridad = itemView.findViewById(R.id.ticket_prioridad);
            cardView = itemView.findViewById(R.id.cardViewTicket);

        }
    }
}



