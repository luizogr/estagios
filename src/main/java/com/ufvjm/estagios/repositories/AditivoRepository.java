package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aditivo;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AditivoRepository extends JpaRepository<Aditivo, UUID> {

    Optional<Aditivo> findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(Estagio estagio, StatusAditivo status);
}
