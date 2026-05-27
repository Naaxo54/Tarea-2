package backend;

public class ExcepcionCarga extends Exception {

    public enum Tipo {
        ARCHIVO_NO_ENCONTRADO,
        FORMATO_INVALIDO,
        DATOS_INCOMPLETOS,
        ERROR_BD,
        VACIO
    }

    private final Tipo tipo;

    public ExcepcionCarga(String mensaje, Tipo tipo) {
        super(mensaje);
        this.tipo = tipo;
    }

    public ExcepcionCarga(String mensaje, Tipo tipo, Throwable causa) {
        super(mensaje, causa);
        this.tipo = tipo;
    }

    public Tipo getTipo() { return tipo; }

    @Override
    public String toString() { return "[" + tipo + "] " + getMessage(); }
}
