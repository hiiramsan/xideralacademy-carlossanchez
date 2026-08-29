package com.example.demoWebflux.model;

import java.time.LocalDateTime;

public record Partido(String id, String equipoLocal, String equipoVisitante, LocalDateTime fecha, String estadio) {
}
