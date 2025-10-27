package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {
}
