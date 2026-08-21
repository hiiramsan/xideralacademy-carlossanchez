# Entregables semana 2:

### Carlos Hiram Sanchez Meneses

## 01-spring-data-jpa

REST API hecha con Spring construida para la gestión de jugadores y equipos de fútbol (soccer players y teams), utilizando **Spring Data JPA** para la persistencia de datos.

**Crear un equipo:**

```json
{
  "name": "Real Madrid",
  "stadium": "Santiago Bernabeu"
}
```

**Crear un jugador:**

```json
{
  "firstName": "Carlos",
  "lastName": "Sanchez",
  "dateOfBirth": "2007-04-01",
  "position": "Midfielder",
  "nationality": "Mexico",
  "team": {
    "id": 1
  }
}
```

## 02-spring-data-mongodb

REST API hecha con Spring para la gestión de productos, utilizando **MongoDB** como base de datos a través de Spring Data MongoDB.

**Crear un producto:**

```json
{
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard with blue switches",
  "price": 89.99,
  "category": "Peripherals",
  "stock": 15
}
```

## 03-inyeccion-dependencias-java

Proyecto en Java que ejemplifica los conceptos de **Inversion of Control (IoC)** y **Dependency Injection (DI)**, mostrando cómo desacoplar clases de sus dependencias para reducir el acoplamiento, aplicar el principio de responsabilidad única y facilitar el testing.