package link360.model;
 

public class Factura {
 
    private int    numFactura;        
    private String numTelefonico;
    private String fecha;
    private String fechaVencimiento;
    private double monto;
    private double impuestos;
    private double descuentosAplicados;
    private int    puntosRedimidos;
    private double montoFinal;
    private String fechaPago;          
    private String estadoPago;        
 
 
    public Factura() {}
 
    public Factura(int numFactura, String numTelefonico, String fecha,
                   String fechaVencimiento, double monto, double impuestos,
                   double descuentosAplicados, int puntosRedimidos,
                   double montoFinal, String fechaPago, String estadoPago) {
        this.numFactura          = numFactura;
        this.numTelefonico       = numTelefonico;
        this.fecha               = fecha;
        this.fechaVencimiento    = fechaVencimiento;
        this.monto               = monto;
        this.impuestos           = impuestos;
        this.descuentosAplicados = descuentosAplicados;
        this.puntosRedimidos     = puntosRedimidos;
        this.montoFinal          = montoFinal;
        this.fechaPago           = fechaPago;
        this.estadoPago          = estadoPago;
    }
 
 
    public int    getNumFactura()                  { return numFactura;            }
    public void   setNumFactura(int v)             { this.numFactura = v;          }
 
    public String getNumTelefonico()               { return numTelefonico;         }
    public void   setNumTelefonico(String v)       { this.numTelefonico = v;       }
 
    public String getFecha()                       { return fecha;                 }
    public void   setFecha(String v)               { this.fecha = v;               }
 
    public String getFechaVencimiento()            { return fechaVencimiento;      }
    public void   setFechaVencimiento(String v)    { this.fechaVencimiento = v;    }
 
    public double getMonto()                       { return monto;                 }
    public void   setMonto(double v)               { this.monto = v;               }
 
    public double getImpuestos()                   { return impuestos;             }
    public void   setImpuestos(double v)           { this.impuestos = v;           }
 
    public double getDescuentosAplicados()         { return descuentosAplicados;   }
    public void   setDescuentosAplicados(double v) { this.descuentosAplicados = v; }
 
    public int    getPuntosRedimidos()             { return puntosRedimidos;       }
    public void   setPuntosRedimidos(int v)        { this.puntosRedimidos = v;     }
 
    public double getMontoFinal()                  { return montoFinal;            }
    public void   setMontoFinal(double v)          { this.montoFinal = v;          }
 
    public String getFechaPago()                   { return fechaPago;             }
    public void   setFechaPago(String v)           { this.fechaPago = v;           }
 
    public String getEstadoPago()                  { return estadoPago;            }
    public void   setEstadoPago(String v)          { this.estadoPago = v;          }
 
    @Override
    public String toString() {
        return "Factura #" + numFactura + " – Tel: " + numTelefonico
               + " – Monto: ₡" + montoFinal + " – " + estadoPago;
    }
}