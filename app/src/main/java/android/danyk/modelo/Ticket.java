package android.danyk.modelo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Ticket implements Parcelable {
    private String titulo;
    private String estado;
    private String prioridad;
    private String descripcion;
    private List<String> imageUris;
    private String idTicket;
    private boolean finalizado;

    public Ticket() {
    }

    public Ticket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUris, String idTicket, boolean finalizado) {        this.titulo = titulo;
        this.estado = estado;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.imageUris = imageUris;
        this.idTicket = idTicket;
        this.finalizado = finalizado;
    }

    protected Ticket(Parcel in) {
        titulo = in.readString();
        estado = in.readString();
        prioridad = in.readString();
        descripcion = in.readString();
        imageUris = in.createStringArrayList();
        idTicket = in.readString();
        finalizado = in.readByte() != 0;
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }

    };

    public Ticket(String titulo, String estado, String prioridad, String descripcion, List<String> imageUrls, String s, String notas) {
    }
    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(estado);
        dest.writeString(prioridad);
        dest.writeString(descripcion);
        dest.writeStringList(imageUris);
        dest.writeString(idTicket);
        dest.writeByte((byte) (finalizado ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}