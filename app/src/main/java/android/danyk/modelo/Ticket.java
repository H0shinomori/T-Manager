package android.danyk.modelo;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Ticket {
    private String titulo;
    private String estado;
    private String prioridad;
    private String descripcion;
    private List<String> imageUris;
    private boolean guardado;

    public Ticket() {
    }

    public Ticket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUris) {
        this.titulo = titulo;
        this.estado = estado;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.imageUris = imageUris;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
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

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }
}
