package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    @Override
    Optional<Professor> findById(UUID uuid);
}
