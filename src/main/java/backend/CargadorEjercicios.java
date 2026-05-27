package backend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargadorEjercicios {

    public List<Ejercicio> desdeCSV(String ruta) throws ExcepcionCarga {
        if (!Files.exists(Paths.get(ruta)))
            throw new ExcepcionCarga("Archivo no encontrado: " + ruta, ExcepcionCarga.Tipo.ARCHIVO_NO_ENCONTRADO);

        List<Ejercicio> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), StandardCharsets.UTF_8))) {

            String encabezado = br.readLine();
            if (encabezado == null || encabezado.isBlank())
                throw new ExcepcionCarga("El archivo está vacío.", ExcepcionCarga.Tipo.VACIO);

            char delimitador = encabezado.contains(";") ? ';' : ',';
            String linea;
            int numLinea = 1;

            while ((linea = br.readLine()) != null) {
                numLinea++;
                if (linea.isBlank()) continue;

                String[] partes = linea.split(String.valueOf(delimitador), -1);
                if (partes.length < 5)
                    throw new ExcepcionCarga(
                        "Línea " + numLinea + ": se esperaban 5 columnas, se encontraron " + partes.length + ".",
                        ExcepcionCarga.Tipo.DATOS_INCOMPLETOS);

                String nombre    = partes[0].trim();
                String tipoStr   = partes[1].trim();
                String nivStr    = partes[2].trim();
                String tiempoStr = partes[3].trim();
                String desc      = partes[4].trim();

                for (String[] par : new String[][]{{nombre,"nombre"},{tipoStr,"tipo"},{nivStr,"intensidad"},{tiempoStr,"tiempo_estimado"},{desc,"descripcion"}})
                    if (par[0].isEmpty())
                        throw new ExcepcionCarga("Línea " + numLinea + ": campo '" + par[1] + "' vacío.", ExcepcionCarga.Tipo.DATOS_INCOMPLETOS);

                try {
                    TipoEjercicio tipo       = TipoEjercicio.desdeTexto(tipoStr);
                    NivelIntensidad intensidad = NivelIntensidad.desdeTexto(nivStr);
                    int tiempo = Integer.parseInt(tiempoStr);
                    if (tiempo <= 0) throw new NumberFormatException();
                    lista.add(new Ejercicio(numLinea - 1, nombre, tipo, intensidad, tiempo, desc));
                } catch (IllegalArgumentException ex) {
                    throw new ExcepcionCarga("Línea " + numLinea + ": " + ex.getMessage(), ExcepcionCarga.Tipo.FORMATO_INVALIDO, ex);
                }
            }

        } catch (IOException ex) {
            throw new ExcepcionCarga("Error al leer el archivo: " + ex.getMessage(), ExcepcionCarga.Tipo.ARCHIVO_NO_ENCONTRADO, ex);
        }

        if (lista.isEmpty())
            throw new ExcepcionCarga("El archivo no contiene ejercicios.", ExcepcionCarga.Tipo.VACIO);

        return lista;
    }

    public List<Ejercicio> desdeBaseDeDatos(String ruta) throws ExcepcionCarga {
        if (!Files.exists(Paths.get(ruta)))
            throw new ExcepcionCarga("Base de datos no encontrada: " + ruta, ExcepcionCarga.Tipo.ARCHIVO_NO_ENCONTRADO);

        List<Ejercicio> lista = new ArrayList<>();

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + ruta)) {
            ResultSet tablas = con.getMetaData().getTables(null, null, "ejercicios", new String[]{"TABLE"});
            if (!tablas.next())
                throw new ExcepcionCarga("La BD no contiene la tabla 'ejercicios'.", ExcepcionCarga.Tipo.ERROR_BD);

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT id, nombre, tipo, intensidad, tiempo_estimado, descripcion FROM ejercicios")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    try {
                        lista.add(new Ejercicio(
                            id,
                            rs.getString("nombre"),
                            TipoEjercicio.desdeTexto(rs.getString("tipo")),
                            NivelIntensidad.desdeTexto(rs.getString("intensidad")),
                            rs.getInt("tiempo_estimado"),
                            rs.getString("descripcion")));
                    } catch (IllegalArgumentException ex) {
                        throw new ExcepcionCarga("Registro id=" + id + ": " + ex.getMessage(), ExcepcionCarga.Tipo.FORMATO_INVALIDO, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ExcepcionCarga("Error de BD: " + ex.getMessage(), ExcepcionCarga.Tipo.ERROR_BD, ex);
        }

        if (lista.isEmpty())
            throw new ExcepcionCarga("La tabla 'ejercicios' está vacía.", ExcepcionCarga.Tipo.VACIO);

        return lista;
    }
}
