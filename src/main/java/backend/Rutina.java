package backend;

import java.util.*;

public class Rutina {

    private final List<Ejercicio> ejercicios;

    public Rutina(List<Ejercicio> ejercicios) {
        this.ejercicios = Collections.unmodifiableList(new ArrayList<>(ejercicios));
    }

    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public int total()                     { return ejercicios.size(); }
    public Ejercicio get(int i)            { return ejercicios.get(i); }

    public int tiempoTotal() {
        return ejercicios.stream().mapToInt(Ejercicio::getTiempoMin).sum();
    }

    public Map<TipoEjercicio, Integer> porTipo() {
        Map<TipoEjercicio, Integer> mapa = new EnumMap<>(TipoEjercicio.class);
        for (TipoEjercicio t : TipoEjercicio.values()) mapa.put(t, 0);
        ejercicios.forEach(e -> mapa.merge(e.getTipo(), 1, Integer::sum));
        return mapa;
    }

    public Map<NivelIntensidad, Integer> porIntensidad() {
        Map<NivelIntensidad, Integer> mapa = new EnumMap<>(NivelIntensidad.class);
        for (NivelIntensidad n : NivelIntensidad.values()) mapa.put(n, 0);
        ejercicios.forEach(e -> mapa.merge(e.getIntensidad(), 1, Integer::sum));
        return mapa;
    }
}
