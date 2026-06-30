package link360.model;
 
/**
 * Model class representing a row in the {@code Plan_Tarifario} table.
 *
 * @author Link360 Project
 */
public class PlanTarifario {
 
    private String codPlan;
    private String nombre;
    private String descripcion;
    private double cuotaMensual;
    private int    gbIncluidos;
    private int    minIncluidos;
    private int    msgIncluidos;
    private double costoExceso;
    private int    idCateg;
 
 
    public PlanTarifario() {}
 
    public PlanTarifario(String codPlan, String nombre, String descripcion,
                         double cuotaMensual, int gbIncluidos, int minIncluidos,
                         int msgIncluidos, double costoExceso, int idCateg) {
        this.codPlan      = codPlan;
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.cuotaMensual = cuotaMensual;
        this.gbIncluidos  = gbIncluidos;
        this.minIncluidos = minIncluidos;
        this.msgIncluidos = msgIncluidos;
        this.costoExceso  = costoExceso;
        this.idCateg      = idCateg;
    }
 
 
    public String getCodPlan()            { return codPlan;       }
    public void   setCodPlan(String v)    { this.codPlan = v;     }
 
    public String getNombre()             { return nombre;        }
    public void   setNombre(String v)     { this.nombre = v;      }
 
    public String getDescripcion()        { return descripcion;   }
    public void   setDescripcion(String v){ this.descripcion = v; }
 
    public double getCuotaMensual()       { return cuotaMensual;  }
    public void   setCuotaMensual(double v){ this.cuotaMensual = v;}
 
    public int  getGbIncluidos()          { return gbIncluidos;   }
    public void setGbIncluidos(int v)     { this.gbIncluidos = v; }
 
    public int  getMinIncluidos()         { return minIncluidos;  }
    public void setMinIncluidos(int v)    { this.minIncluidos = v;}
 
    public int  getMsgIncluidos()         { return msgIncluidos;  }
    public void setMsgIncluidos(int v)    { this.msgIncluidos = v;}
 
    public double getCostoExceso()        { return costoExceso;   }
    public void   setCostoExceso(double v){ this.costoExceso = v; }
 
    public int  getIdCateg()              { return idCateg;       }
    public void setIdCateg(int v)         { this.idCateg = v;     }
 
    @Override
    public String toString() {
        return codPlan + " – " + nombre + " (₡" + cuotaMensual + "/mes)";
    }
}