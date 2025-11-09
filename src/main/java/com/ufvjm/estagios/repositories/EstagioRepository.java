package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstagioRepository extends JpaRepository<Estagio, UUID> {

    boolean existsByAlunoAndStatusEstagioIn(Aluno aluno, List<StatusEstagio> statuses);

    List<Estagio> findByAluno(Aluno aluno);

    List<Estagio> findByOrientador(Professor professor);

    List<Estagio> findByStatusEstagio(StatusEstagio status);
}
