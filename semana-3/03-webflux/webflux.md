# Spring Webflux

Spring Webflux es un framework web reactivo y no bloqueante del ecosistema de Spring, que fue diseñado para manejar alta concurrencia con pocos recursos. 

Es parte de la programación reactiva, la cual es un paradigma donde la aplicacion reacciona automaticamente a cambios en los datos o eventos, es decir, el servidor permite procesar operaciones asincronas y de I/O sin bloquear recursos mientras se espera su resultado.

La programacion reactiva trabaja con flujos de datos que llegan a lo largo del tiempo. 

En nuestro proyecto, representamos el flujo o stream reactivo con los eventos de un partido (goles, tarjetas, pitido final), en `FutbolReactivoService.eventosEnVivo(partidoId)`

Un flujo tiene:
> Publisher -> Subscriber

El **publisher** produce datos, el **suscriber** consume esos datos

### Mono y Flux

- `Mono<T>` representa **una operacion que eventualmente producira cero o un elemento**

    Ejemplo: 

    `Mono<User>` significa: 0 usuarios o 1 usuario

    En el proyecto:
    ```
    // 0 o 1 jugador
    public Mono<Equipo> buscarEquipoPorId(String id) {
    return Mono.justOrEmpty(equipos.get(id))
            .delayElement(LATENCIA_SIMULADA); // simula 5s de latencia, sin bloquear
}   
    ```

- `Flux<T>` representa una **operacion que eventualmente producira cero, uno o muchos elementos**

    Ejemplo: 

    `Flux<User>` puede producir

    ```
    User 1
    User 2
    User 3
    User 4
    ...
    ```

    En el proyecto:
    ```
    // Puede haber cero, uno o varios jugadores
    public Flux<Jugador> jugadoresDeEquipo(String equipoId) {
        return Flux.fromIterable(jugadores.values())
                .filter(j -> j.equipoId().equals(equipoId))
                .delayElements(Duration.ofMillis(300)); // emite uno cada 300ms
    }
    ```


| Tipo      | Cantidad de elementos |
| --------- | --------------------: |
| `Mono<T>` |                 0 o 1 |
| `Flux<T>` |                 0 a N |


### Problema que resuelve

Un servidor Spring MVC clasico usa el **modelo thread-per-request**: cada peticion ocupa un hilo del pool y ese hilo se queda ocupado. Esto funciona bien mientras laconcurrencia sea baja, el problema aparece cuando el numero de peticiones supera el numero de hilos disponibles.

El proyecto lo reproduce de esta forma:

`FutbolBloqueanteService.buscarEquipoPorIdBloqueante(id)` simula una consulta de 5 segundos usando Thread.sleep(5000).
Se expone en GET /api/bloqueante/equipos/{id}.

Lo que hace webflux es que, en lugar de que  un hilo se quede esperando, describe la operacion como **algo que retomara mas adelante** y libera el hilo para atender otras peticiones.

### Comportamiento Lazy

Una propiedad que tienen `Mono` y `Flux` es que son `lazy` , esto significa que definir un `Mono` o `Flux` no ejecuta nada, solo describe una receta de lo que hay que hacer cuando alguien se suscriba. Un `Mono` es una receta, no hace la accion sino que define el como

```
CREAR MONO
     ↓
NO BUSCA
     ↓
subscribe()
     ↓
AHORA BUSCA
```

### Cuando no usar Webflux

1. C**uando lo tardado es calcular, no esperar:** Por ejemplo procesar imagenes, calculos numericos pesados, compresion. Aqui ser reactivo no ayuda pues el cuello de botella es la CPU, no los hilos esperando I/O
2. **Dependencias bloqueantes:** si la aplicacion usa un driver/libreria que internamente bloquea, meterlo dentro de un pipeline reactivo suele ser peor
3. Baja concurrencia: Si el servicio no recibira muchas peticiones simultaneas, el modelo normal de Spring MVC es suficiente y bastante mas simple

## Como correr el proyecto

1. Es un proyecto Spring Boot de Java asi que se requiere Java 21 y Maven, se puede correr en cualquier IDE o bien, en una terminal y en la raiz del proyecto, compilar el proyecto

```bash
mvn compile
```

2. Despues empaquetar:

```bash
mvn package
```

3. Correr el jar:

```bash
java -jar target/03-webflux-snapshot.jar
```

### Endpoints del proyecto
 
| Endpoint | Tipo | Qué demuestra |
|---|---|---|
| `GET /api/reactivo/equipos/{id}` | `Mono<Equipo>` | Un único resultado, no bloqueante |
| `GET /api/reactivo/equipos/{id}/jugadores` | `Flux<Jugador>` | Varios elementos emitidos progresivamente |
| `GET /api/reactivo/partidos` | `Flux<Partido>` | Colección completa, no bloqueante |
| `GET /api/reactivo/partidos/{id}/eventos-en-vivo` | `Flux<EventoPartido>` | Flujo real en el tiempo, streaming al cliente |
| `GET /api/bloqueante/equipos/{id}` | `String` (bloqueante) | El "antipatrón": mismo trabajo, con `Thread.sleep`, para comparar |