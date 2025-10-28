package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
}
