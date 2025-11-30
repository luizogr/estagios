package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aditivo;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AditivoRepository extends JpaRepository<Aditivo, UUID> {

    Optional<Aditivo> findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(Estagio estagio, StatusAditivo status);

    List<Aditivo> findByStatus(StatusAditivo status);

    @Query("SELECT a FROM Aditivo a WHERE a.status = :status AND a.estagio.orientador = :orientador")
    List<Aditivo> findByStatusAndEstagioOrientador(StatusAditivo status, Professor orientador);
}
