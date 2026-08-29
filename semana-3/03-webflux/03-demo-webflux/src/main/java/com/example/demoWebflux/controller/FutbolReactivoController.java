package com.example.demoWebflux.controller;

import com.example.demoWebflux.model.Equipo;
import com.example.demoWebflux.model.EventoPartido;
import com.example.demoWebflux.model.Jugador;
import com.example.demoWebflux.model.Partido;
import com.example.demoWebflux.service.FutbolReactivoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reactivo")
public class FutbolReactivoController {

    private final FutbolReactivoService servicio;

    public FutbolReactivoController(FutbolReactivoService servicio) {
        this.servicio = servicio;
    }

    // GET /api/reactivo/equipos/real-madrid
    @GetMapping("/equipos/{id}")
    public Mono<Equipo> obtenerEquipo(@PathVariable String id) {
        return servicio.buscarEquipoPorId(id);
    }

    // GET /api/reactivo/equipos/real-madrid/jugadores
    @GetMapping("/equipos/{id}/jugadores")
    public Flux<Jugador> jugadoresDeEquipo(@PathVariable String id) {
        return servicio.jugadoresDeEquipo(id);
    }

    // GET /api/reactivo/partidos
    @GetMapping("/partidos")
    public Flux<Partido> todosLosPartidos() {
        return servicio.todosLosPartidos();
    }

    // GET /api/reactivo/partidos/p1/eventos-en-vivo
    @GetMapping(value = "/partidos/{id}/eventos-en-vivo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoPartido> eventosEnVivo(@PathVariable String id) {
        return servicio.eventosEnVivo(id);
    }
}