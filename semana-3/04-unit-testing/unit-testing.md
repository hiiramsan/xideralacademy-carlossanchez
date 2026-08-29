# 04 - Unit Testing: JUnit y Mockito

El **unit testing** o pruebas unitarias son el proceso en que se prueba una unidad funcional individual para verificar que funciona correctamente de manera aislada. Esa unidad normalmentees una funcion, un metodo o un componente pequeño.

## JUnit

JUnit es un framework de pruebas unitarias de codigo abierto que permite escribir y ejecutar pruebas en Java automatizadas.

Por ejemplo, si tienes una clase calculadora con un metodo sumar:

```
public class Calculadora {
    public int sumar(int a, int b) {
        return a + b;
    }
}
```

Con **JUnit** la probarias de esta forma:

```
class CalculadoraTest {
    // le dices a junit que esto es una prueba
    @Test
    void deberiaSumarDosNumeros() {
        Calculadora calculadora = new Calculadora();
        int resultado = calculadora.sumar(2, 3);
        // comprueba que el resultado sea el esperado 
        assertEquals(5, resultado);
    }
}

```

### Aserciones en JUnit

Las aserciones son la forma de comprobar que algo sale como esperamos.


| Aserción            | ¿Qué comprueba?                 | Ejemplo                                                   |
| ------------------- | ------------------------------- | --------------------------------------------------------- |
| `assertEquals()`    | Que dos valores sean iguales    | `assertEquals(5, resultado)`                              |
| `assertNotEquals()` | Que dos valores sean diferentes | `assertNotEquals(10, resultado)`                          |
| `assertTrue()`      | Que una condición sea verdadera | `assertTrue(usuario.isActivo())`                          |
| `assertFalse()`     | Que una condición sea falsa     | `assertFalse(usuario.isBloqueado())`                      |
| `assertNull()`      | Que el valor sea `null`         | `assertNull(usuario)`                                     |
| `assertNotNull()`   | Que el valor **no** sea `null`  | `assertNotNull(usuario)`                                  |
| `assertThrows()`    | Que se lance una excepción      | `assertThrows(MiException.class, () -> service.buscar())` |

### Ciclo de vida JUnit

| Anotación     | ¿Cuándo se ejecuta?                    | Uso típico               |
| ------------- | -------------------------------------- | ------------------------ |
| `@BeforeAll`  | Una vez **antes de todos** los tests   | Inicialización general   |
| `@BeforeEach` | Antes de **cada test**                 | Preparar datos/objetos   |
| `@Test`       | Ejecuta la prueba                      | Probar el comportamiento |
| `@AfterEach`  | Después de **cada test**               | Limpiar datos/recursos   |
| `@AfterAll`   | Una vez **después de todos** los tests | Limpieza general         |

Ejemplo: en el proyecto, en la clase `UsuarioServiceTest` usamos un `@BeforeEach` para setear un usuario para usarlo en cada prueba si se requiere:

```
@BeforeEach
    void setUp() {
        usuarioExistente = new Usuario(1L, "Ana", "ana@mail.com", true);
    }
```

## Mockito

Por otro lado, Mockito es un framework de codigo abierto para crear pruebas unitarias y es utilizado principalmente para crear objetos simulados (mocks) en pruebas unitarias.

Permite aislar el codigo que quieres probar imitando el comportamiento de dependencias reales, como bases de datos, servicios web o APIs externas.

Ejemplo de uso: 

 Si tenemos un servicio:

```
public class UsuarioService {
    private UsuarioRepository repository;
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
    public Usuario buscarUsuario(Long id) {
        return repository.findById(id);
    }
}
```
Aqui `UsuarioService` depende de un `UsuarioRepository`. Si quisieramos hacer una prueba unitaria de `UsuarioService` no queremos necesariamente conectarnosa una base de datos real, queremos probar solo el servicio.

Aqui es donde entra **Mockito**, podemos crear un `mock` del repository:

```
@Mock
UsuarioRepository repository;
```

Basicamente es un objeto falso que nosotros controlamos pues podemos indicarle cosas como: "Si alguien te pide el usuario con ID 1, devuelve este usuario", ejemplo:

```
when(repository.findById(1L))
    .thenReturn(usuario);
```

De esta manera, nuestra prueba unitaria queda:

```
@Test
void deberiaBuscarUsuario() {

    Usuario usuario = new Usuario(1L, "Juan");

    when(repository.findById(1L))
        .thenReturn(usuario);

    Usuario resultado = service.buscarUsuario(1L);

    assertEquals("Juan", resultado.getNombre());
}
```

### En el proyecto demo:

Ejemplo de test:

```

@Test
    @DisplayName("Registrar usuario: camino feliz")
    void registrarUsuario_datosValidos_devuelveUsuarioGuardado() {
        // given = es basicamente preparar los mocks
        when(usuarioRepository.existsByEmail("nuevo@mail.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacion -> {
                    Usuario u = invocacion.getArgument(0);
                    return new Usuario(10L, u.getNombre(), u.getEmail(), u.isActivo());
                });

        // when = ejecutar el metodo
        Usuario resultado = usuarioService.registrarUsuario("Luis", "nuevo@mail.com");

        // then = comprobar resultado
        assertAll("usuario registrado",
                () -> assertNotNull(resultado.getId()),
                () -> assertEquals("Luis", resultado.getNombre()),
                () -> assertTrue(resultado.isActivo())
        );

        // verificamos que el repositorio fue realmente invocado
        verify(usuarioRepository).existsByEmail("nuevo@mail.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

```

¿Por qué mockear el repositorio y no el resto?

`UsuarioRepository` se mockea porque representa un colaborador **externo**:
en una app real hablaría con una base de datos. En un test unitario no
queremos levantar una base de datos ni depender de su estado; solo queremos
probar la *lógica* de `UsuarioService` (validaciones, decisiones,
excepciones) asumiendo que el repositorio hace lo que le decimos.

Lo que **no** se mockea en el proyecto:

- **`Usuario`**: es un objeto de dominio simple (POJO), sin dependencias
  externas ni lógica compleja. Mockearlo no ahorraría nada y haría el test
  menos realista; se usa una instancia real construida a mano.
- **`UsuarioService`** (la clase bajo test, con `@InjectMocks`): nunca se
  mockea la clase que se está probando, porque entonces se estaría testeando
  un objeto falso en vez del código real.