package link360.model;

public class Servicio {

    private String codServicio;
    private String nombre;
    private String descripcion;
    private double costoMensual;
    private String categoria;

    public Servicio() {
    }

    public Servicio(String codServicio, String nombre, String descripcion,
            double costoMensual, String categoria) {
        this.codServicio = codServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoMensual = costoMensual;
        this.categoria = categoria;
    }

    public String getCodServicio() {
        return codServicio;
    }

    public void setCodServicio(String v) {
        this.codServicio = v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String v) {
        this.descripcion = v;
    }

    public double getCostoMensual() {
        return costoMensual;
    }

    public void setCostoMensual(double v) {
        this.costoMensual = v;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String v) {
        this.categoria = v;
    }

    @Override
    public String toString() {
        return codServicio + " – " + nombre;
    }
}
