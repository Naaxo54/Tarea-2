package backend;

import java.util.*;
import java.util.stream.Collectors;

public class GeneradorRutina {

    public Rutina generar(List<Ejercicio> todos,
                          Map<TipoEjercicio, Map<NivelIntensidad, Integer>> requisitos,
                          Set<String> historial) {

        List<Ejercicio> seleccionados = new ArrayList<>();
        Random rand = new Random();

        for (Map.Entry<TipoEjercicio, Map<NivelIntensidad, Integer>> entradaTipo : requisitos.entrySet()) {
            for (Map.Entry<NivelIntensidad, Integer> entradaNivel : entradaTipo.getValue().entrySet()) {
                int cantidad = entradaNivel.getValue();
                if (cantidad <= 0) continue;

                TipoEjercicio tipo = entradaTipo.getKey();
                NivelIntensidad nivel = entradaNivel.getKey();

                List<Ejercicio> candidatos = todos.stream()
                    .filter(e -> e.getTipo() == tipo && e.getIntensidad() == nivel
                              && !seleccionados.contains(e))
                    .collect(Collectors.toList());

                List<Ejercicio> sinHistorial = candidatos.stream()
                    .filter(e -> !historial.contains(e.getNombre().toLowerCase()))
                    .collect(Collectors.toList());

                List<Ejercicio> conHistorial = candidatos.stream()
                    .filter(e -> historial.contains(e.getNombre().toLowerCase()))
                    .collect(Collectors.toList());

                List<Ejercicio> elegidos = aleatorio(sinHistorial, cantidad, rand);
                if (elegidos.size() < cantidad)
                    elegidos.addAll(aleatorio(conHistorial, cantidad - elegidos.size(), rand));

                if (elegidos.size() < cantidad)
                    throw new IllegalArgumentException(String.format(
                        "No hay suficientes ejercicios de tipo '%s' con intensidad '%s' " +
                        "(pedidos: %d, disponibles: %d).", tipo, nivel, cantidad, elegidos.size()));

                seleccionados.addAll(elegidos);
            }
        }

        if (seleccionados.isEmpty())
            throw new IllegalArgumentException("No se seleccionaron ejercicios. Verifique los requisitos.");

        return new Rutina(seleccionados);
    }

    private List<Ejercicio> aleatorio(List<Ejercicio> lista, int n, Random rand) {
        if (lista.isEmpty()) return new ArrayList<>();
        List<Ejercicio> copia = new ArrayList<>(lista);
        Collections.shuffle(copia, rand);
        return new ArrayList<>(copia.subList(0, Math.min(n, copia.size())));
    }
}
