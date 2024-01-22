package android.danyk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaAdaptador extends RecyclerView.Adapter<ListaAdaptador.ViewHolder> {
    private List<Ticket> datos;
    private LayoutInflater inflater;
    private Context context;

    public ListaAdaptador(List<Ticket> itemList, Fragment context) {
        this.inflater = LayoutInflater.from(context.getContext());
        this.context = context.getContext();
        this.datos = itemList;
    }

    @Override
    public ListaAdaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.ticket_recycle_view, null);
        return new ListaAdaptador.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdaptador.ViewHolder holder, final int position) {
        Ticket tickets = datos.get(position);
        holder.titulo.setText(tickets.getTitulo());
        holder.estado.setText(tickets.getEstado());
        holder.prioridad.setText(tickets.getPrioridad());
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setItems(List<Ticket> items) {
        datos = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado, prioridad;
        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.ticket_titulo);
            estado = itemView.findViewById(R.id.ticket_estado);
            prioridad = itemView.findViewById(R.id.ticket_prioridad);
        }

        public void bindData(Ticket ticket) {
            titulo.setText(ticket.getTitulo());
            estado.setText(ticket.getEstado());
            prioridad.setText(ticket.getPrioridad());
        }
    }
}
