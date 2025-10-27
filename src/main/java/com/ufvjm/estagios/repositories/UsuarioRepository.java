package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmailInstitucional(String email);
}
