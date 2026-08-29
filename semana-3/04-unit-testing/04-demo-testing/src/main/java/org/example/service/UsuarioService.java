package org.example.service;

import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;

import java.util.NoSuchElementException;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(String nombre, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Usuario nuevoUsuario = new Usuario(null, nombre, email, true);
        return usuarioRepository.save(nuevoUsuario);
    }

    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));

        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

}
