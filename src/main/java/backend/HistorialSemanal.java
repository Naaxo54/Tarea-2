package backend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class HistorialSemanal {

    private static final String ARCHIVO = "historial_semanal.txt";

    public static void guardar(List<Ejercicio> ejercicios) {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(ARCHIVO, false), StandardCharsets.UTF_8))) {
            ejercicios.forEach(e -> pw.println(e.getNombre().toLowerCase()));
        } catch (IOException e) {
            System.err.println("No se pudo guardar el historial: " + e.getMessage());
        }
    }

    public static Set<String> cargar() {
        Set<String> historial = new HashSet<>();
        Path ruta = Paths.get(ARCHIVO);
        if (!Files.exists(ruta)) return historial;
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null)
                if (!linea.isBlank()) historial.add(linea.trim().toLowerCase());
        } catch (IOException e) {
            System.err.println("No se pudo leer el historial: " + e.getMessage());
        }
        return historial;
    }
}
