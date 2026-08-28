package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    private static final int NUM_TAREAS = 10;
    private static final int NUM_HILOS = 4;

    // Define la carpeta donde se guardaran los archivos de salida
    private static final Path CARPETA_SALIDA = Paths.get("salida");

    // Define la ruta del archivo de log dentro de la carpeta de salida
    private static final Path ARCHIVO_LOG = CARPETA_SALIDA.resolve("log.txt");

    // Crea la carpeta de salida si no existe.
    private static final Path ARCHIVO_SERIALIZADO = CARPETA_SALIDA.resolve("resultados.ser");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(CARPETA_SALIDA);

        AtomicInteger contador = new AtomicInteger(0);
        List<ResultadoTarea> resultados = Collections.synchronizedList(new ArrayList<>());

        // Crea 4 threads que se reparten las tareas
        ExecutorService pool = Executors.newFixedThreadPool(NUM_HILOS);

        for (int i = 1; i <= NUM_TAREAS; i++) {
            pool.submit(new ProcesadorTarea(i, contador, resultados));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("Todas las tareas terminaron. Total: " + contador.get());

        escribirLog(resultados);
        serializarResultados(resultados);
        List<ResultadoTarea> leidos = leerResultadosSerializados();

        System.out.println("\n--- Resultados leídos desde " + ARCHIVO_SERIALIZADO + " ---");
        leidos.forEach(System.out::println);
    }

    // Escribe los resultados de las tareas en el archivo de log
    private static void escribirLog(List<ResultadoTarea> resultados) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (ResultadoTarea r : resultados) {
            lineas.add(r.toString());
        }
        Files.write(ARCHIVO_LOG, lineas, StandardCharsets.UTF_8);
    }

    // guarda la lista completa de resultados como un unico objeto binario
    private static void serializarResultados(List<ResultadoTarea> resultados) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(ARCHIVO_SERIALIZADO)))) {
            oos.writeObject(new ArrayList<>(resultados));
        }
    }

    // reconstruye tal cual, con todos sus objetos
    @SuppressWarnings("unchecked")
    private static List<ResultadoTarea> leerResultadosSerializados() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(ARCHIVO_SERIALIZADO)))) {
            return (List<ResultadoTarea>) ois.readObject();
        }
    }

}
