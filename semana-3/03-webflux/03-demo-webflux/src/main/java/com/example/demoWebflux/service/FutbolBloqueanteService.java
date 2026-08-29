package com.example.demoWebflux.service;

import org.springframework.stereotype.Service;

@Service
public class FutbolBloqueanteService {

    private static final long LATENCIA_MS = 5000;

    public String buscarEquipoPorIdBloqueante(String id) {
        try {
            Thread.sleep(LATENCIA_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Equipo " + id + " (resuelto de forma BLOQUEANTE)";
    }
}