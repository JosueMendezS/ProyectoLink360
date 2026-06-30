package link360.model;
 

public class LineaMovil {
 
    private String numTelefonico;
    private String cedula;
    private String tipoLinea;      
    private String tecnologia;    
    private String fechaActivacion;
    private String estadoLinea;   
    private String tipoSIM;        
 
 
    public LineaMovil() {}
 
    public LineaMovil(String numTelefonico, String cedula, String tipoLinea,
                      String tecnologia, String fechaActivacion,
                      String estadoLinea, String tipoSIM) {
        this.numTelefonico  = numTelefonico;
        this.cedula         = cedula;
        this.tipoLinea      = tipoLinea;
        this.tecnologia     = tecnologia;
        this.fechaActivacion = fechaActivacion;
        this.estadoLinea    = estadoLinea;
        this.tipoSIM        = tipoSIM;
    }
 
 
    public String getNumTelefonico()           { return numTelefonico;   }
    public void   setNumTelefonico(String v)   { this.numTelefonico = v; }
 
    public String getCedula()                  { return cedula;          }
    public void   setCedula(String v)          { this.cedula = v;        }
 
    public String getTipoLinea()               { return tipoLinea;       }
    public void   setTipoLinea(String v)       { this.tipoLinea = v;     }
 
    public String getTecnologia()              { return tecnologia;      }
    public void   setTecnologia(String v)      { this.tecnologia = v;    }
 
    public String getFechaActivacion()         { return fechaActivacion; }
    public void   setFechaActivacion(String v) { this.fechaActivacion = v; }
 
    public String getEstadoLinea()             { return estadoLinea;     }
    public void   setEstadoLinea(String v)     { this.estadoLinea = v;   }
 
    public String getTipoSIM()                 { return tipoSIM;         }
    public void   setTipoSIM(String v)         { this.tipoSIM = v;       }
 
    @Override
    public String toString() {
        return numTelefonico + " (" + tipoLinea + " – " + estadoLinea + ")";
    }
}