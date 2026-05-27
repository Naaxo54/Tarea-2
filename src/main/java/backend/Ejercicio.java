package backend;

public class Ejercicio {

    private final int id;
    private final String nombre;
    private final TipoEjercicio tipo;
    private final NivelIntensidad intensidad;
    private final int tiempoMin;
    private final String descripcion;

    public Ejercicio(int id, String nombre, TipoEjercicio tipo,
                     NivelIntensidad intensidad, int tiempoMin, String descripcion) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (tiempoMin <= 0)
            throw new IllegalArgumentException("El tiempo debe ser mayor a 0.");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción no puede estar vacía.");

        this.id = id;
        this.nombre = nombre.trim();
        this.tipo = tipo;
        this.intensidad = intensidad;
        this.tiempoMin = tiempoMin;
        this.descripcion = descripcion.trim();
    }

    public int getId()                    { return id; }
    public String getNombre()             { return nombre; }
    public TipoEjercicio getTipo()        { return tipo; }
    public NivelIntensidad getIntensidad(){ return intensidad; }
    public int getTiempoMin()             { return tiempoMin; }
    public String getDescripcion()        { return descripcion; }
}
