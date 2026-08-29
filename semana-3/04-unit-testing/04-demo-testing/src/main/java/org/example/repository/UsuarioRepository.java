package org.example.repository;

import org.example.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    boolean existsByEmail(String email);

}
