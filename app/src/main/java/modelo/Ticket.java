package modelo;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Ticket {
    private String titulo;
    private String estado;
    private String prioridad;
    private String descripcion;

    public Ticket(){

    }

    public Ticket(String titulo, String estado, String prioridad, String descripcion) {
        this.titulo = titulo;
        this.estado = estado;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
