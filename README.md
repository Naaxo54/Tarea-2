# Sistema de Rutinas de Entrenamiento Personalizado

Aplicación de escritorio con interfaz gráfica (Java Swing) que permite gestionar y generar
rutinas de entrenamiento personalizadas a partir de un catálogo de ejercicios cargado desde
un archivo CSV o una base de datos SQLite.

---

## Índice

1. [Requisitos del sistema](#requisitos-del-sistema)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [Compilación y ejecución](#compilación-y-ejecución)
4. [Formato del archivo de ejercicios (CSV)](#formato-del-archivo-de-ejercicios-csv)
5. [Formato de la base de datos (SQLite)](#formato-de-la-base-de-datos-sqlite)
6. [Flujo de uso de la aplicación](#flujo-de-uso-de-la-aplicación)
7. [Arquitectura del sistema](#arquitectura-del-sistema)
8. [Alcances y supuestos](#alcances-y-supuestos)
9. [Manejo de errores](#manejo-de-errores)

---

## Requisitos del sistema

| Componente | Versión mínima |
|---|---|
| Java JDK | 11 |
| Apache Maven | 3.6 |
| Sistema operativo | Windows 10 / macOS 10.14 / Linux (con entorno gráfico) |

> **Nota**: La aplicación utiliza Java Swing, que requiere un entorno de escritorio con
> servidor de visualización (X11 en Linux, Aqua en macOS, Win32 en Windows).

---

## Estructura del proyecto

```
java-app/
├── pom.xml                              # Configuración Maven (dependencias, build)
├── compile.sh                           # Script de compilación
├── run.sh                               # Script de ejecución
├── weekly_history.txt                   # Historial semanal (se genera al usar la app)
├── data/
│   ├── ejercicios.csv                   # Archivo CSV de ejemplo con 20 ejercicios
│   ├── schema.sql                       # Script SQL para crear la BD SQLite
│   └── crear_db.sh                      # Script para generar ejercicios.db
└── src/
    └── main/
        └── java/
            ├── Main.java                # Punto de entrada
            ├── backend/                 # Lógica de negocio
            │   ├── Exercise.java
            │   ├── ExerciseType.java
            │   ├── IntensityLevel.java
            │   ├── AppEvent.java
            │   ├── AppObserver.java
            │   ├── AppController.java
            │   ├── ExerciseLoader.java
            │   ├── TrainingRoutine.java
            │   ├── RoutineGenerator.java
            │   ├── WeeklyHistory.java
            │   └── ExerciseLoadException.java
            └── frontend/                # Interfaz gráfica
                ├── MainWindow.java
                ├── LoadDataWindow.java
                ├── GenerateRoutineWindow.java
                ├── ReviewRoutineWindow.java
                └── SummaryWindow.java
```

---

## Compilación y ejecución

### Opción A — Scripts de conveniencia (Linux/macOS)

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x compile.sh run.sh data/crear_db.sh

# Compilar
./compile.sh

# Ejecutar
./run.sh
```

### Opción B — Maven directamente

```bash
# Desde el directorio java-app/
mvn clean package
java -jar target/rutinas-entrenamiento.jar
```

### Opción C — Desde un IDE (IntelliJ, Eclipse, VS Code)

1. Importar `java-app/` como proyecto Maven existente.
2. Ejecutar la clase `Main` (en `src/main/java/Main.java`).
3. El IDE descargará las dependencias automáticamente.

---

## Formato del archivo de ejercicios (CSV)

El archivo debe ser un CSV con la **primera fila como encabezado**.
Se acepta tanto coma (`,`) como punto y coma (`;`) como delimitador.

### Encabezado requerido

```
nombre,tipo,intensidad,tiempo_estimado,descripcion
```

### Descripción de columnas

| Columna | Tipo | Valores válidos | Descripción |
|---|---|---|---|
| `nombre` | Texto | Cualquier texto no vacío | Nombre del ejercicio |
| `tipo` | Texto | `cardiovascular` \| `fuerza` | Tipo de ejercicio (insensible a mayúsculas) |
| `intensidad` | Texto | `baja` \| `media` \| `alta` | Nivel de intensidad (insensible a mayúsculas) |
| `tiempo_estimado` | Entero | Número entero > 0 | Duración en minutos |
| `descripcion` | Texto | Cualquier texto no vacío | Instrucciones de ejecución |

### Ejemplo de archivo CSV válido

```csv
nombre,tipo,intensidad,tiempo_estimado,descripcion
Salto de tijera,cardiovascular,baja,5,Párate derecho con los pies juntos...
Flexión de brazos,fuerza,media,10,Colócate en posición de plancha...
Burpee básico,cardiovascular,alta,12,Desde posición de pie baja al suelo...
```

> El archivo de ejemplo `data/ejercicios.csv` contiene **20 ejercicios** listos para usar.

### Cómo cargar el archivo

1. Abrir la aplicación.
2. Hacer clic en **"Cargar Ejercicios"**.
3. En el selector, elegir **"Archivo CSV"** como tipo de fuente.
4. Hacer clic en **"Examinar..."** y navegar hasta el archivo, o escribir la ruta directamente.
5. Hacer clic en **"Cargar"**.

---

## Formato de la base de datos (SQLite)

La base de datos debe ser un archivo SQLite (`.db`) con una tabla llamada `ejercicios`.

### Schema requerido

```sql
CREATE TABLE ejercicios (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre           TEXT    NOT NULL,
    tipo             TEXT    NOT NULL,  -- 'cardiovascular' | 'fuerza'
    intensidad       TEXT    NOT NULL,  -- 'baja' | 'media' | 'alta'
    tiempo_estimado  INTEGER NOT NULL,  -- minutos, debe ser > 0
    descripcion      TEXT    NOT NULL
);
```

### Crear la BD de ejemplo

```bash
# Desde el directorio java-app/
chmod +x data/crear_db.sh
./data/crear_db.sh
# Genera: data/ejercicios.db con los mismos 20 ejercicios del CSV
```

### Cómo cargar la base de datos

1. Abrir la aplicación.
2. Hacer clic en **"Cargar Ejercicios"**.
3. En el selector, elegir **"Base de datos SQLite"** como tipo de fuente.
4. Hacer clic en **"Examinar..."** y seleccionar el archivo `.db`.
5. Hacer clic en **"Cargar"**.

---

## Flujo de uso de la aplicación

```
[Ventana Principal]
      │
      ▼
[Ventana de Carga]  ─── seleccionar CSV o BD ───►  carga asincrónica
      │                                                   │
      ◄────────────── notificación via Observer ──────────┘
      │
      ▼  (estadísticas visibles: total, por tipo, por intensidad, tiempo total)
      │
[Ventana de Generación]  ─── configurar ejercicios por tipo e intensidad
      │                                                   │
      ◄────────────── notificación via Observer ──────────┘
      │
      ▼
[Ventana de Revisión]  ─── navegar ejercicio por ejercicio
      │    ◄ Volver / Siguiente ►
      │    (Volver deshabilitado en el primero)
      │    (Siguiente → "Resumen" en el último)
      │
      ▼
[Ventana de Resumen]  ─── totales por tipo, intensidad y tiempo
```

---

## Arquitectura del sistema

### Paquetes

| Paquete | Responsabilidad |
|---|---|
| `backend` | Lógica de negocio: modelos, carga, generación, historial, validación |
| `frontend` | Interfaz gráfica Swing: ventanas, formularios, navegación |

### Patrón Observer (notificación-suscripción)

La comunicación entre paquetes sigue el patrón **Observer**:

- `AppObserver` — interfaz con método `onEvent(AppEvent, Object)`.
- `AppController` — Subject/Publisher; mantiene la lista de observadores y los notifica
  **siempre en el EDT de Swing** mediante `SwingUtilities.invokeLater`.
- Las ventanas del frontend implementan `AppObserver`, se suscriben al abrir y se
  desuscriben al cerrarse.

### Operaciones asincrónicas

Las operaciones costosas (carga de archivo/BD, generación de rutina) se ejecutan en
**hilos de fondo** (`Thread` con nombre descriptivo). La notificación al frontend
se realiza en el EDT para garantizar seguridad en el acceso a componentes Swing.

### Restricción de no repetición semanal

El sistema persiste los nombres de los ejercicios de la última rutina generada en el
archivo `weekly_history.txt` (en el directorio de trabajo del programa).

- Al generar una nueva rutina, el generador **excluye primero** los ejercicios del historial.
- Si no hay suficientes ejercicios nuevos para cubrir la demanda, completa con ejercicios
  del historial (notificado implícitamente; la rutina se genera igual).
- Al guardar la nueva rutina, el historial se reemplaza por completo.

---

## Alcances y supuestos

1. **Tipos de ejercicio**: Se reconocen exactamente dos tipos: `cardiovascular` y `fuerza`.
   Cualquier otro valor en el archivo/BD genera un error de formato.

2. **Niveles de intensidad**: Se reconocen exactamente tres niveles: `baja`, `media` y `alta`.

3. **Tiempo estimado**: Debe ser un número entero positivo (minutos). No se aceptan decimales
   ni cero.

4. **Codificación de archivos**: El archivo CSV debe estar codificado en **UTF-8**.

5. **Delimitador CSV**: Se aceptan coma (`,`) y punto y coma (`;`). El sistema detecta
   automáticamente el delimitador a partir de la línea de encabezado.

6. **Nombre de tabla en BD**: El sistema busca específicamente la tabla llamada `ejercicios`.
   Una BD sin esa tabla genera un error de formato.

7. **Historial semanal**: El historial se almacena en `weekly_history.txt` en el directorio
   desde donde se ejecuta el programa (directorio de trabajo). Si el archivo no existe, se
   asume que no hay historial previo (primera rutina).

8. **Selección de ejercicios**: Dentro de cada combinación (tipo, intensidad), los ejercicios
   se seleccionan **aleatoriamente** para garantizar variedad entre sesiones.

9. **Disponibilidad de ejercicios**: Si el catálogo no tiene suficientes ejercicios para
   satisfacer los requisitos configurados (incluso usando el historial), se muestra un error
   descriptivo indicando el tipo e intensidad sin cobertura suficiente.

10. **Entorno gráfico**: La aplicación requiere un entorno de escritorio (servidor de
    visualización). No funciona en modo headless (por ejemplo, en servidores sin pantalla).

---

## Manejo de errores

Todos los errores de carga se gestionan mediante la excepción personalizada
`ExerciseLoadException`, que categoriza el error mediante un enum `ErrorType`:

| Tipo de error | Causa |
|---|---|
| `FILE_NOT_FOUND` | El archivo o BD no existe o no es legible |
| `INVALID_FORMAT` | Tipo/intensidad/tiempo con valor fuera del dominio |
| `INCOMPLETE_DATA` | Columna obligatoria vacía o fila con menos de 5 columnas |
| `DATABASE_ERROR` | Error de conexión SQL o tabla inexistente |
| `EMPTY_FILE` | Archivo con solo encabezado o BD sin registros |
| `UNKNOWN` | Error inesperado |

Los errores se muestran al usuario en la interfaz gráfica mediante diálogos de error
descriptivos, indicando la línea o registro problemático cuando aplica.
