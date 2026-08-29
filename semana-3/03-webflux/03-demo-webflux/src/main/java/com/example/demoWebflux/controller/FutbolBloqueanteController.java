package com.example.demoWebflux.controller;

import com.example.demoWebflux.service.FutbolBloqueanteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint deliberadamente "mal hecho"
 */
@RestController
@RequestMapping("/api/bloqueante")
public class FutbolBloqueanteController {

    private final FutbolBloqueanteService servicio;

    public FutbolBloqueanteController(FutbolBloqueanteService servicio) {
        this.servicio = servicio;
    }

    // GET /api/bloqueante/equipos/real-madrid
    @GetMapping("/equipos/{id}")
    public String obtenerEquipoBloqueante(@PathVariable String id) {
        return servicio.buscarEquipoPorIdBloqueante(id);
    }
}