package org.example;

import java.io.Serializable;

public class ResultadoTarea implements Serializable {

    // identifica version de la clase para serializacion
    private static final long serialVersionUID = 1L;

    private final int idTarea;
    private final String hiloQueProceso;
    private final long tiempoProcesoMs;

    // no se serializa
    private transient String tokenSesionTemporal;

    public ResultadoTarea(int idTarea, String hiloQueProceso, long tiempoProcesoMs) {
        this.idTarea = idTarea;
        this.hiloQueProceso = hiloQueProceso;
        this.tiempoProcesoMs = tiempoProcesoMs;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public String getHiloQueProceso() {
        return hiloQueProceso;
    }

    public long getTiempoProcesoMs() {
        return tiempoProcesoMs;
    }


    @Override
    public String toString() {
        return String.format(
                "Tarea #%d procesada por %s en %d ms",
                idTarea, hiloQueProceso, tiempoProcesoMs
        );
    }

}
