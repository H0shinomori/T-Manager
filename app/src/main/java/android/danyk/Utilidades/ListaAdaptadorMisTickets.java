package android.danyk.Utilidades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.danyk.DAO.TicketDAO;
import android.danyk.DAO.UserDAO;
import android.danyk.R;
import android.danyk.dbManager.FirebaseSingleton;
import android.danyk.modelo.Ticket;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ListaAdaptadorMisTickets extends RecyclerView.Adapter<ListaAdaptadorMisTickets.ViewHolder> {
    private List<Ticket> datos;
    private final LayoutInflater inflater;
    private final Context context;
    private final List<String> idsMisTickets;
    private String selectedFilter;

    public ListaAdaptadorMisTickets(List<Ticket> itemList, Context context, List<String> idsMisTickets, String selectedFilter) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.datos = itemList;
        this.idsMisTickets = idsMisTickets;
        this.selectedFilter = selectedFilter != null ? selectedFilter : "";
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

        if (idsMisTickets.contains(ticket.getIdTicket())) {
            holder.titulo.setText(ticket.getTitulo());
            holder.estado.setText(ticket.getEstado());
            holder.prioridad.setText(ticket.getPrioridad());

            if (ticket.getPrioridad().equals("Alta")) {
                int color = ContextCompat.getColor(context, R.color.color_prioridad_alta);
                holder.prioridad.setTextColor(color);
            } else if (ticket.getPrioridad().equals("Media")) {
                int color = ContextCompat.getColor(context, R.color.color_prioridad_media);
                holder.prioridad.setTextColor(color);
            } else if (ticket.getPrioridad().equals("Baja")) {
                int color = ContextCompat.getColor(context, R.color.color_prioridad_baja);
                holder.prioridad.setTextColor(color);
            }

            if (ticket.getEstado().equals("Finalizado") && selectedFilter.equals("Finalizados")) {
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarInformacionTicket(ticket, true);
                    }
                });
            } else {
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarInformacionTicket(ticket, false);
                    }
                });
            }

            holder.iconoGuardado.setImageResource(R.drawable.eliminar_ticket);
            holder.iconoGuardado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDAO userDAO = new UserDAO();
                    TicketDAO ticketDAO = new TicketDAO();
                    ticketDAO.eliminarMiTicket(userDAO.getUserID(), ticket.getIdTicket());
                }
            });
        }
    }
    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
        notifyDataSetChanged();
    }
    @SuppressLint("InflateParams")
    private void mostrarInformacionTicket(Ticket ticket, boolean esFinalizado) {
        View dialogView;
        if (esFinalizado) {
            dialogView = inflater.inflate(R.layout.ticket_preview2, null);
        } else {
            dialogView = inflater.inflate(R.layout.ticket_preview3, null);
        }

        TextView tituloPreview = dialogView.findViewById(R.id.tituloTextViewPreview);
        TextView estadoPreview = dialogView.findViewById(R.id.estadoTextViewPreview);
        TextView prioridadPreview = dialogView.findViewById(R.id.prioridadTextViewPreview);
        TextView descripcionPreview = dialogView.findViewById(R.id.descripcionTextViewPreview);
        LinearLayout layoutVistaPreviaImagen = dialogView.findViewById(R.id.layout_vistaPreviaImagen);
        ImageButton botonCerrarDialog = dialogView.findViewById(R.id.cerrar_dialog);
        TextView creadoPor = dialogView.findViewById(R.id.creadoPorTextViewPreview);
        TextView completadoPor = dialogView.findViewById(R.id.completadoPorTextViewPreview);
        TextView notasView = dialogView.findViewById(R.id.notasTextViewPreview);

        tituloPreview.setText(ticket.getTitulo());
        estadoPreview.setText(ticket.getEstado());
        prioridadPreview.setText(ticket.getPrioridad());
        descripcionPreview.setText(ticket.getDescripcion());
        creadoPor.setText(ticket.getCreadoPor());
        completadoPor.setText(ticket.getHechoPor());

        String notas = ticket.getNotas();
        if (notas != null) {
            notasView.setText(ticket.getNotas());
        }

        layoutVistaPreviaImagen.removeAllViews();

        if (ticket.getImageUris() != null && !ticket.getImageUris().isEmpty()) {
            for (String imageUrl : ticket.getImageUris()) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        dipToPixels(context, 350),
                        dipToPixels(context, 500)
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
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.setView(dialogView);
        dialog.show();

        botonCerrarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
