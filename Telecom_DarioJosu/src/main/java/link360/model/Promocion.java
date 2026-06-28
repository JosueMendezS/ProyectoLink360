package link360.model;

public class Promocion {
    private String codPromocion;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private double pctDescuento;
    private int    idTipoPromo;

    public Promocion() {}
    public Promocion(String codPromocion, String nombre, String descripcion,
                     String fechaInicio, String fechaFin, double pctDescuento, int idTipoPromo) {
        this.codPromocion = codPromocion; this.nombre = nombre;
        this.descripcion = descripcion;   this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;         this.pctDescuento = pctDescuento;
        this.idTipoPromo = idTipoPromo;
    }
    public String getCodPromocion()           { return codPromocion; }
    public void   setCodPromocion(String v)   { codPromocion = v; }
    public String getNombre()                 { return nombre; }
    public void   setNombre(String v)         { nombre = v; }
    public String getDescripcion()            { return descripcion; }
    public void   setDescripcion(String v)    { descripcion = v; }
    public String getFechaInicio()            { return fechaInicio; }
    public void   setFechaInicio(String v)    { fechaInicio = v; }
    public String getFechaFin()               { return fechaFin; }
    public void   setFechaFin(String v)       { fechaFin = v; }
    public double getPctDescuento()           { return pctDescuento; }
    public void   setPctDescuento(double v)   { pctDescuento = v; }
    public int    getIdTipoPromo()            { return idTipoPromo; }
    public void   setIdTipoPromo(int v)       { idTipoPromo = v; }
    @Override public String toString()        { return codPromocion + " – " + nombre; }
}