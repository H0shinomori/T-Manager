package android.danyk.Utilidades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.danyk.Actividades.actividad_editarTicket;
import android.danyk.R;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.danyk.modelo.Ticket;

import com.bumptech.glide.Glide;

public class ListaAdaptador extends RecyclerView.Adapter<ListaAdaptador.ViewHolder> {
    private List<Ticket> datos;
    private final List<Ticket> guardados;
    private final LayoutInflater inflater;
    private final Context context;

    public ListaAdaptador(List<Ticket> itemList, Fragment context) {
        this.inflater = LayoutInflater.from(context.getContext());
        this.context = context.getContext();
        this.datos = itemList;
        this.guardados = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListaAdaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.ticket_recycle_view, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdaptador.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Ticket tickets = datos.get(position);
        holder.titulo.setText(tickets.getTitulo());
        holder.estado.setText(tickets.getEstado());
        holder.prioridad.setText(tickets.getPrioridad());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AQUI
                View dialogView = inflater.inflate(R.layout.ticket_preview, null);
                TextView tituloPreview = dialogView.findViewById(R.id.tituloTextViewPreview);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView estadoPreview = dialogView.findViewById(R.id.estadoTextViewPreview);
                TextView prioridadPreview = dialogView.findViewById(R.id.prioridadTextViewPreview);
                TextView descripcionPreview = dialogView.findViewById(R.id.descripcionTextViewPreview);
                LinearLayout layoutVistaPreviaImagen = dialogView.findViewById(R.id.layout_vistaPreviaImagen);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                Button botonEditar = dialogView.findViewById(R.id.boton_editarTicket);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                ImageButton botonCerrarDialog = dialogView.findViewById(R.id.cerrar_dialog);

                tituloPreview.setText(tickets.getTitulo());
                estadoPreview.setText(tickets.getEstado());
                prioridadPreview.setText(tickets.getPrioridad());
                descripcionPreview.setText(tickets.getDescripcion());

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
                Drawable background = ContextCompat.getDrawable(context, R.drawable.redondear_bordes);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);
                dialog.setView(dialogView);
                dialog.show();

                botonCerrarDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                botonEditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, actividad_editarTicket.class);
                        context.startActivity(intent);
                    }
                });
            }
        });


        holder.iconoGuardado.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {


            }
        });
    }

    private int dipToPixels(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setItems(List<Ticket> items) {
        datos = items;
    }

    public List<Ticket> getGuardados() {
        return guardados;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado, prioridad;
        CardView cardView;
        ImageView iconoGuardado;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.ticket_titulo);
            estado = itemView.findViewById(R.id.ticket_estado);
            prioridad = itemView.findViewById(R.id.ticket_prioridad);
            cardView = itemView.findViewById(R.id.cardViewTicket);
            iconoGuardado = itemView.findViewById(R.id.icono_ticket_guardar);
        }

    }

    public interface OnTicketActionListener {
        void onTicketGuardado(Ticket ticket, boolean guardado);
    }
}
