package android.danyk.Utilidades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.danyk.Actividades.actividad_editarTicket;
import android.danyk.DAO.TicketDAO;
import android.danyk.DAO.UserDAO;
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

import java.util.List;
import java.util.Objects;

import android.danyk.modelo.Ticket;

import com.bumptech.glide.Glide;

public class ListaAdaptador extends RecyclerView.Adapter<ListaAdaptador.ViewHolder> {
    private List<Ticket> datos;
    private final LayoutInflater inflater;
    private final Context context;
    private List<String> idsTickets;
    private final List<Ticket> guardados;
    public void setIdsTickets(List<String> idsTickets) {
        this.idsTickets = idsTickets;
    }
    public ListaAdaptador(List<Ticket> itemList, Fragment context, List<String> idsTickets, List<Ticket> guardados) {
        this.inflater = LayoutInflater.from(context.getContext());
        this.context = context.getContext();
        this.datos = itemList;
        this.idsTickets = idsTickets;
        this.guardados = guardados;
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
        String ticketId = tickets.getIdTicket();

        if (!idsTickets.contains(ticketId)){
            holder.titulo.setText(tickets.getTitulo());
            holder.estado.setText(tickets.getEstado());
            holder.prioridad.setText(tickets.getPrioridad());
            holder.iconoGuardado.setImageResource(R.drawable.ic_bookmark_noguardado);

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
                    View dialogView = inflater.inflate(R.layout.ticket_preview, null);
                    TextView tituloPreview = dialogView.findViewById(R.id.tituloTextViewPreview);
                    TextView estadoPreview = dialogView.findViewById(R.id.estadoTextViewPreview);
                    TextView prioridadPreview = dialogView.findViewById(R.id.prioridadTextViewPreview);
                    TextView descripcionPreview = dialogView.findViewById(R.id.descripcionTextViewPreview);
                    LinearLayout layoutVistaPreviaImagen = dialogView.findViewById(R.id.layout_vistaPreviaImagen);
                    Button botonEditar = dialogView.findViewById(R.id.boton_editarTicket);
                    ImageButton botonCerrarDialog = dialogView.findViewById(R.id.cerrar_dialog);
                    TextView creadoPor = dialogView.findViewById(R.id.creadoPorTextViewPreview);

                    tituloPreview.setText(tickets.getTitulo());
                    estadoPreview.setText(tickets.getEstado());
                    prioridadPreview.setText(tickets.getPrioridad());
                    descripcionPreview.setText(tickets.getDescripcion());
                    creadoPor.setText(tickets.getCreadoPor());

                    layoutVistaPreviaImagen.removeAllViews();

                    if (tickets.getImageUris() != null && !tickets.getImageUris().isEmpty()) {
                        for (String imageUrl : tickets.getImageUris()) {
                            ImageView imageView = new ImageView(context);
                            assert context != null;
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
                    botonEditar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Ticket ticket = datos.get(position);
                            Intent intent = new Intent(context, actividad_editarTicket.class);
                            intent.putExtra("ticket", ticket);
                            intent.putExtra("creadoPor", tickets.getCreadoPor());
                            context.startActivity(intent);
                        }
                    });
                }
            });
            holder.iconoGuardado.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    Ticket ticket = datos.get(position);
                    notifyItemChanged(position);
                    UserDAO userDAO = new UserDAO();
                    String currentUser = userDAO.getUserID();
                    TicketDAO ticketDAO = new TicketDAO();
                    ticketDAO.agregarTicketGuardado(currentUser,ticket.getIdTicket());

                }
            });
        }
    }
    private int dipToPixels(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public int getItemCount() {
        return datos.size();
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
}