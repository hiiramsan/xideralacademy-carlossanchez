package com.example.demoWebflux.model;

import java.time.LocalDateTime;

public record EventoPartido(String partidoId, int minuto, String tipo, String descripcion, LocalDateTime timestamp) {
}
