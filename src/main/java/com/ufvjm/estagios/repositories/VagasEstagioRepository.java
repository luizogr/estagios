package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.VagasEstagio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VagasEstagioRepository extends JpaRepository<VagasEstagio, UUID> {
}
