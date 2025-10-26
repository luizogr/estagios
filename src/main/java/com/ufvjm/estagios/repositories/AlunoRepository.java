package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepositorie extends JpaRepository<Aluno, Long> {
}
