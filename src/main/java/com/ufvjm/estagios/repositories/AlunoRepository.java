package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {
    @Override
    Optional<Aluno> findById(UUID uuid);
}
