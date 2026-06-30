package link360.model;

public class Distrito {

    private int codDistrito;
    private String nombreDistrito;
    private int codCanton;

    public Distrito(int codDistrito, String nombreDistrito, int codCanton) {
        this.codDistrito = codDistrito;
        this.nombreDistrito = nombreDistrito;
        this.codCanton = codCanton;
    }

    public int getCodDistrito() {
        return codDistrito;
    }

    public String getNombreDistrito() {
        return nombreDistrito;
    }

    public int getCodCanton() {
        return codCanton;
    }

    @Override
    public String toString() {
        return codDistrito + " – " + nombreDistrito;
    }
}
