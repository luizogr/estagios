package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aditivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AditivoRepository extends JpaRepository<Aditivo, UUID> {
}
