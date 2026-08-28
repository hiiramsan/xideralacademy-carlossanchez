# 01 - Core avanzado: threading, manejo de archivos y serialización

## Threading

Un `thread` es una linea de ejecucion independiente dentro de un mismo programa. En java, varios threads pueden ejecutarse al mismo tiempo para atender diferentes tareas.

En el proyecto de ejemplo se incluye la clase `ProcesadorTarea` el cual implementa de `Runnable` (el trabajo que ejecuta cada thread). En la clase `main` se crea el pool de threads y entrega las tareas.

```java
// Crea 4 threads que se reparten las tareas
ExecutorService pool = Executors.newFixedThreadPool(4);

for (int i = 1; i <= NUM_TAREAS; i++) {
    pool.submit(new ProcesadorTarea(i, contador, resultados));
}

pool.shutdown();
pool.awaitTermination(30, TimeUnit.SECONDS);
```

Sin threading, el programa procesaria las 10 tareas de forma secuencial, es decir, la tarea 2 no empieza hasta que se termine la 1.

## Manejo de archivos

El manejo de archivos permite que nuestro programa en Java pueda persistir informacion, por ejemplo, leer datos de un txt al iniciar o guardar resultados al terminar. Sin escribir a disco, todo el trabajo de los threads (los resultados calculados) desaparecería en cuanto el programa termina.

Java ofrece diferentes librerias para trabajar con archivos:

- **`java.io`**: API tradicional de Java para entrada/salida. Introduce el `File` (representar archivos y directorios), `FileInputStream` y `FileOutputStream` (leer y escribir bytes), `FileReader` y `FileWriter` (leer y escribir caracteres), `BufferedInputStream` y `BufferedOutputStream`, entre otros.

```java
File archivo = new File("datos.txt");

try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
    String linea;
    while ((linea = reader.readLine()) != null) {
        System.out.println(linea);
    }
}
```

- **`java.nio`**: API moderna de archivos, aparece en Java 1.4 e incluye conceptos como Buffer, ByteBuffer, Channel, FileChannel, Path y Files.

- **`java.nio.file`**: API moderna de archivos, incluye Path (para representar una ruta), Files (operar sobre archivos/directorios), FileSystem (para representar un sistema de archivos), etc.

```java
Path path = Path.of("datos.txt");

String contenido = Files.readString(path);

System.out.println(contenido);
```

En el proyecto de ejemplo, estamos utilizando el manejo de archivos en la clase `Main` para escribir logs sobre los resultados de las tareas:

```java
// Define la carpeta donde se guardaran los archivos de salida
private static final Path CARPETA_SALIDA = Paths.get("salida");

// Define la ruta del archivo de log dentro de la carpeta de salida
private static final Path ARCHIVO_LOG = CARPETA_SALIDA.resolve("log.txt");

// Crea la carpeta de salida si no existe.
Files.createDirectories(CARPETA_SALIDA);

// Escribe los resultados de las tareas en el archivo de log
private static void escribirLog(List<ResultadoTarea> resultados) throws IOException {
    List<String> lineas = new ArrayList<>();

    for (ResultadoTarea r : resultados) {
        lineas.add(r.toString());
    }

    Files.write(ARCHIVO_LOG, lineas, StandardCharsets.UTF_8);
}
```

## Serialización

Serializar se refiere a convertir un objeto Java en una secuencia de bytes que se pueden guardar en disco o enviar por la red, para despues someterlo a la deserializacion, lo cual es a la inversa con el objetivo de reconstruir el objeto original a partir de los bytes. En java podemos serializar un objeto implementando la interfaz `Serializable` y usando `ObjectOutputStream` (para escribir) y `ObjectInputStream` (para leer).

En el caso de nuestro proyecto, guardar el log de texto es util para que una persona lo lea pero si otro programa quiere trabajar con esos resultados como objetos `ResultadoTarea`, la serializacion evita reescribir toda la logica de convertir texto a objeto.

```java
// Implementamos Serializable
public class ResultadoTarea implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idTarea;
    private final String hiloQueProceso;
    private final long tiempoProcesoMs;

    private transient String tokenSesionTemporal;
    ...
}
```

En el `Main`:

```java
// guarda la lista completa de resultados como un unico objeto binario
private static void serializarResultados(List<ResultadoTarea> resultados) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(
            new BufferedOutputStream(Files.newOutputStream(ARCHIVO_SERIALIZADO)))) {
        oos.writeObject(new ArrayList<>(resultados));
    }
}

// reconstruye tal cual, con todos sus objetos
private static List<ResultadoTarea> leerResultadosSerializados() throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(
            new BufferedInputStream(Files.newInputStream(ARCHIVO_SERIALIZADO)))) {
        return (List<ResultadoTarea>) ois.readObject();
    }
}
```

> 💡 **`serialVersionUID`**: Identifica la version de una clase serializable. Al deserializar, Java lo compara para comprobar que la clase actual sea compatible con el objeto guardado. Si no coincide, lanza `InvalidClassException`.
>
> **`transient`**: si se marca con esta expresion, el campo no se serializa. Al deserializar, queda con su valor por defecto (null, 0, false, etc).

## Como correr el proyecto

1. Es un proyecto simple de Java asi que se requiere Java 21 y Maven, se puede correr en cualquier IDE o bien, en una terminal y en la raiz del proyecto, compilar el proyecto

```bash
mvn compile
```

2. Despues empaquetar:

```bash
mvn package
```

3. Correr el jar:

```bash
java -jar target/01-demo-core-avanzado-snapshot.jar
```

## Estructura

```
01-demo-core-avanzado/
└── src/
    └── com/example/threadfile/
        ├── Main.java              → punto de entrada, hilos + archivos + serialización
        ├── ProcesadorTarea.java   → Runnable ejecutado por cada hilo
        ├── ResultadoTarea.java    → clase Serializable con serialVersionUID y campo transient
```

Al ejecutar `Main`, se genera además una carpeta `salida/` con:

```
salida/
├── log.txt          → resultados en texto plano
└── resultados.ser   → resultados serializados como objetos Java
```
