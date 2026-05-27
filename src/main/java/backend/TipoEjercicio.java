package backend;

public enum TipoEjercicio {
    CARDIOVASCULAR("cardiovascular"),
    FUERZA("fuerza");

    private final String nombre;

    TipoEjercicio(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }

    public static TipoEjercicio desdeTexto(String v) {
        switch (v.trim().toLowerCase()) {
            case "cardiovascular": return CARDIOVASCULAR;
            case "fuerza":         return FUERZA;
            default: throw new IllegalArgumentException("Tipo desconocido: '" + v + "'");
        }
    }

    @Override
    public String toString() { return nombre; }
}
