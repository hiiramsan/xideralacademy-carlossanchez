import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;
import org.example.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    // colaborador mockeado
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioExistente;

    @BeforeAll
    static void init() {
        System.out.println("Esto se imprime solo una vez, antes de todos los tests");
    }

    // se ejecuta antes de cada test
    @BeforeEach
    void setUp() {
        usuarioExistente = new Usuario(1L, "Ana", "ana@mail.com", true);
    }

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

    @Test
    @DisplayName("Registrar usuario: email duplicado lanza excepción y no guarda")
    void registrarUsuario_emailDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario("Ana Duplicada", "ana@mail.com"));

        assertEquals("Ya existe un usuario con ese email", ex.getMessage());

        // camino de error, el save no se llama
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar usuario: email vacío lanza excepción sin tocar el repositorio")
    void registrarUsuario_emailVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario("Pedro", " "));

        // ni siquiera se debería haber consultado el repositorio
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Desactivar usuario: camino feliz")
    void desactivarUsuario_existente_quedaInactivo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.desactivarUsuario(1L);

        assertFalse(resultado.isActivo());
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    @DisplayName("Desactivar usuario: id inexistente lanza NoSuchElementException")
    void desactivarUsuario_noExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> usuarioService.desactivarUsuario(99L));

        // camino de error: no debe intentar guardar nada
        verify(usuarioRepository, never()).save(any());
    }

}
