package android.danyk;

public class Ticket {
    public String titulo;
    public String estado;
    public String prioridad;

    public Ticket() {

    }

    public Ticket(String titulo, String estado, String prioridad) {
        this.titulo = titulo;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
