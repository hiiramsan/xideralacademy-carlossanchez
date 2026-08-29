package com.example.demoWebflux.service;

import com.example.demoWebflux.model.Equipo;
import com.example.demoWebflux.model.EventoPartido;
import com.example.demoWebflux.model.Jugador;
import com.example.demoWebflux.model.Partido;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class FutbolReactivoService {

    // simulamos una latencia de 5 segundos
    private static final Duration LATENCIA_SIMULADA = Duration.ofMillis(5000);

    private final Map<String, Equipo> equipos = new ConcurrentHashMap<>();
    private final Map<String, Jugador> jugadores = new ConcurrentHashMap<>();
    private final Map<String, Partido> partidos = new ConcurrentHashMap<>();

    public FutbolReactivoService() {
        cargarDatosDePrueba();
    }


    public Mono<Equipo> buscarEquipoPorId(String id) {
        return Mono.justOrEmpty(equipos.get(id))
                .delayElement(LATENCIA_SIMULADA)
                .doOnSubscribe(s -> log("Alguien se suscribió a buscarEquipoPorId(" + id + ")"))
                .doOnNext(e -> log("Equipo resuelto: " + e.nombre()));
    }

    public Flux<Jugador> jugadoresDeEquipo(String equipoId) {
        return Flux.fromIterable(jugadores.values())
                .filter(j -> j.equipoId().equals(equipoId))
                .delayElements(Duration.ofMillis(300));
    }


    public Flux<Partido> todosLosPartidos() {
        int total = Math.max(partidos.size(), 1);
        return Flux.fromIterable(partidos.values())
                .delayElements(LATENCIA_SIMULADA.dividedBy(total));
    }

    // stream -> demostracion de stream reactivo
    public Flux<EventoPartido> eventosEnVivo(String partidoId) {
        List<EventoPartido> guion = List.of(
                new EventoPartido(partidoId, 12, "GOL", "¡Gol del equipo local!", null),
                new EventoPartido(partidoId, 34, "TARJETA_AMARILLA", "Amonestación al mediocampista", null),
                new EventoPartido(partidoId, 58, "GOL", "Empate transitorio", null),
                new EventoPartido(partidoId, 77, "GOL", "El visitante toma la delantera", null),
                new EventoPartido(partidoId, 90, "FINAL", "Termina el encuentro", null)
        );

        return Flux.fromIterable(guion)
                .delayElements(Duration.ofSeconds(2))
                .map(e -> new EventoPartido(e.partidoId(), e.minuto(), e.tipo(), e.descripcion(), LocalDateTime.now()));
    }

    private void log(String msg) {
        System.out.println("[hilo: " + Thread.currentThread().getName() + "] " + msg);
    }

    private void cargarDatosDePrueba() {
        equipos.put("real-madrid", new Equipo("real-madrid", "Real Madrid", "España"));
        equipos.put("barcelona", new Equipo("barcelona", "FC Barcelona", "España"));
        equipos.put("river-plate", new Equipo("river-plate", "River Plate", "Argentina"));

        jugadores.put("j1", new Jugador("j1", "Jude Bellingham", "real-madrid", 5, "Mediocampista"));
        jugadores.put("j2", new Jugador("j2", "Vinícius Júnior", "real-madrid", 7, "Delantero"));
        jugadores.put("j3", new Jugador("j3", "Lamine Yamal", "barcelona", 10, "Delantero"));
        jugadores.put("j4", new Jugador("j4", "Pedri", "barcelona", 8, "Mediocampista"));

        partidos.put("p1", new Partido("p1", "real-madrid", "barcelona",
                LocalDateTime.now().plusDays(7), "Santiago Bernabéu"));
        partidos.put("p2", new Partido("p2", "river-plate", "barcelona",
                LocalDateTime.now().plusDays(14), "Monumental"));
    }
}
