package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Estagio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstagioRepository extends JpaRepository<Estagio, UUID> {
}
