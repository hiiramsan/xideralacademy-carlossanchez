package org.example;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcesadorTarea implements Runnable {

    private final int idTarea;
    private final AtomicInteger contadorGlobal;
    private final List<ResultadoTarea> resultadosCompartidos;

    public ProcesadorTarea(int idTarea, AtomicInteger contadorGlobal,
                           List<ResultadoTarea> resultadosCompartidos) {
        this.idTarea = idTarea;
        this.contadorGlobal = contadorGlobal;
        this.resultadosCompartidos = resultadosCompartidos;
    }

    @Override
    public void run() {
        long inicio = System.currentTimeMillis();
        try {
            // Simula trabajo real (cálculo, llamada a red, acceso a BD, etc.)
            Thread.sleep(50 + (long) (Math.random() * 150));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long duracion = System.currentTimeMillis() - inicio;

        int totalProcesadas = contadorGlobal.incrementAndGet();

        ResultadoTarea resultado = new ResultadoTarea(
                idTarea, Thread.currentThread().getName(), duracion
        );

        synchronized (resultadosCompartidos) {
            resultadosCompartidos.add(resultado);
        }

        System.out.printf("[%s] Tarea %d lista. Total procesadas hasta ahora: %d%n",
                Thread.currentThread().getName(), idTarea, totalProcesadas);
    }
}