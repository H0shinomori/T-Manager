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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class ListaAdaptadorMisTickets extends RecyclerView.Adapter<ListaAdaptadorMisTickets.ViewHolder> {
    private List<Ticket> datos;
    private final LayoutInflater inflater;
    private final Context context;
    private final List<String> idsMisTickets;

    public ListaAdaptadorMisTickets(List<Ticket> itemList, Context context, List<String> idsMisTickets) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.datos = itemList;
        this.idsMisTickets = idsMisTickets;
    }

    @NonNull
    @Override
    public ListaAdaptadorMisTickets.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.ticket_recycle_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdaptadorMisTickets.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Ticket ticket = datos.get(position);

        // Verificar si el ticket está guardado por el usuario
        if (idsMisTickets.contains(ticket.getIdTicket())) {
            holder.titulo.setText(ticket.getTitulo());
            holder.estado.setText(ticket.getEstado());
            holder.prioridad.setText(ticket.getPrioridad());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})

                @Override
                public void onClick(View v) {
                    View dialogView = inflater.inflate(R.layout.ticket_preview, null);
                    TextView tituloPreview = dialogView.findViewById(R.id.tituloTextViewPreview);
                    TextView estadoPreview = dialogView.findViewById(R.id.estadoTextViewPreview);
                    TextView prioridadPreview = dialogView.findViewById(R.id.prioridadTextViewPreview);
                    TextView descripcionPreview = dialogView.findViewById(R.id.descripcionTextViewPreview);
                    LinearLayout layoutVistaPreviaImagen = dialogView.findViewById(R.id.layout_vistaPreviaImagen);
                    Button botonEditar = dialogView.findViewById(R.id.boton_editarTicket);
                    ImageButton botonCerrarDialog = dialogView.findViewById(R.id.cerrar_dialog);

                    tituloPreview.setText(ticket.getTitulo());
                    estadoPreview.setText(ticket.getEstado());
                    prioridadPreview.setText(ticket.getPrioridad());
                    descripcionPreview.setText(ticket.getDescripcion());

                    layoutVistaPreviaImagen.removeAllViews();

                    if (ticket.getImageUris() != null && !ticket.getImageUris().isEmpty()) {
                        for (String imageUrl : ticket.getImageUris()) {
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

                    botonCerrarDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            holder.iconoGuardado.setImageResource(R.drawable.eliminar_ticket);
            holder.iconoGuardado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //AQUI VA UN MÉTODO PARA ELIMINAR EL TICKET.
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    private int dipToPixels(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado, prioridad;
        ImageView iconoGuardado;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.ticket_titulo);
            estado = itemView.findViewById(R.id.ticket_estado);
            prioridad = itemView.findViewById(R.id.ticket_prioridad);
            cardView = itemView.findViewById(R.id.cardViewTicket);
            iconoGuardado = itemView.findViewById(R.id.icono_ticket_guardar);

        }
    }
}
