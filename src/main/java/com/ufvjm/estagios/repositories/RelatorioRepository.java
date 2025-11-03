package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RelatorioRepository extends JpaRepository<Relatorio, UUID> {
}
