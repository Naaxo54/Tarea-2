package backend;

public enum NivelIntensidad {
    BAJA("baja"),
    MEDIA("media"),
    ALTA("alta");

    private final String nombre;

    NivelIntensidad(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }

    public static NivelIntensidad desdeTexto(String v) {
        switch (v.trim().toLowerCase()) {
            case "baja":  return BAJA;
            case "media": return MEDIA;
            case "alta":  return ALTA;
            default: throw new IllegalArgumentException("Intensidad desconocida: '" + v + "'");
        }
    }

    @Override
    public String toString() { return nombre; }
}
