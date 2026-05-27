package backend;

import javax.swing.SwingUtilities;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Controlador {

    private static Controlador instancia;

    private final List<Observador> observadores = new CopyOnWriteArrayList<>();
    private final CargadorEjercicios cargador   = new CargadorEjercicios();
    private final GeneradorRutina generador      = new GeneradorRutina();

    private List<Ejercicio> ejercicios = new ArrayList<>();
    private Rutina rutinaActual        = null;
    private String fuenteDatos         = "";

    private Controlador() {}

    public static synchronized Controlador getInstance() {
        if (instancia == null) instancia = new Controlador();
        return instancia;
    }

    public void suscribir(Observador o)   { if (o != null && !observadores.contains(o)) observadores.add(o); }
    public void desuscribir(Observador o) { observadores.remove(o); }

    private void notificar(Evento evento, Object datos) {
        SwingUtilities.invokeLater(() -> observadores.forEach(o -> o.alRecibir(evento, datos)));
    }

    public void cargarDesdeArchivo(String ruta) {
        new Thread(() -> {
            try {
                ejercicios = cargador.desdeCSV(ruta);
                fuenteDatos = ruta;
                rutinaActual = null;
                notificar(Evento.EJERCICIOS_CARGADOS, Collections.unmodifiableList(ejercicios));
            } catch (ExcepcionCarga ex) {
                notificar(Evento.ERROR_CARGA, ex.toString());
            }
        }, "hilo-carga-csv").start();
    }

    public void cargarDesdeBaseDeDatos(String ruta) {
        new Thread(() -> {
            try {
                ejercicios = cargador.desdeBaseDeDatos(ruta);
                fuenteDatos = ruta;
                rutinaActual = null;
                notificar(Evento.EJERCICIOS_CARGADOS, Collections.unmodifiableList(ejercicios));
            } catch (ExcepcionCarga ex) {
                notificar(Evento.ERROR_CARGA, ex.toString());
            }
        }, "hilo-carga-bd").start();
    }

    public void generarRutina(Map<TipoEjercicio, Map<NivelIntensidad, Integer>> requisitos) {
        if (ejercicios.isEmpty()) {
            notificar(Evento.ERROR_GENERACION, "No hay ejercicios cargados.");
            return;
        }
        new Thread(() -> {
            try {
                Set<String> historial = HistorialSemanal.cargar();
                rutinaActual = generador.generar(ejercicios, requisitos, historial);
                HistorialSemanal.guardar(rutinaActual.getEjercicios());
                notificar(Evento.RUTINA_GENERADA, rutinaActual);
            } catch (IllegalArgumentException ex) {
                notificar(Evento.ERROR_GENERACION, ex.getMessage());
            }
        }, "hilo-generacion").start();
    }

    public List<Ejercicio> getEjercicios()  { return Collections.unmodifiableList(ejercicios); }
    public Rutina getRutinaActual()         { return rutinaActual; }
    public String getFuenteDatos()          { return fuenteDatos; }
    public boolean hayEjercicios()          { return !ejercicios.isEmpty(); }

    public Map<String, Object> estadisticas() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", ejercicios.size());
        Map<TipoEjercicio, Integer> porTipo = new EnumMap<>(TipoEjercicio.class);
        Map<NivelIntensidad, Integer> porNivel = new EnumMap<>(NivelIntensidad.class);
        int tiempo = 0;
        for (TipoEjercicio t : TipoEjercicio.values()) porTipo.put(t, 0);
        for (NivelIntensidad n : NivelIntensidad.values()) porNivel.put(n, 0);
        for (Ejercicio e : ejercicios) {
            porTipo.merge(e.getTipo(), 1, Integer::sum);
            porNivel.merge(e.getIntensidad(), 1, Integer::sum);
            tiempo += e.getTiempoMin();
        }
        stats.put("porTipo", porTipo);
        stats.put("porNivel", porNivel);
        stats.put("tiempoTotal", tiempo);
        return stats;
    }
}
