package link360.model;

public class Cliente {

    private String cedula;
    private String nombre;
    private String apellidos;
    private String fechaIngreso;
    private String tipoCliente;
    private String direccion;
    private int codDistrito;

    public Cliente() {
    }

    public Cliente(String cedula, String nombre, String apellidos,
            String fechaIngreso, String tipoCliente,
            String direccion, int codDistrito) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaIngreso = fechaIngreso;
        this.tipoCliente = tipoCliente;
        this.direccion = direccion;
        this.codDistrito = codDistrito;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String v) {
        this.cedula = v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String v) {
        this.apellidos = v;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String v) {
        this.fechaIngreso = v;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String v) {
        this.tipoCliente = v;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String v) {
        this.direccion = v;
    }

    public int getCodDistrito() {
        return codDistrito;
    }

    public void setCodDistrito(int v) {
        this.codDistrito = v;
    }

    @Override
    public String toString() {
        return cedula + " – " + nombre + " " + apellidos;
    }
}
