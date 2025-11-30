package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.enums.StatusRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RelatorioRepository extends JpaRepository<Relatorio, UUID> {

    long countByEstagio(Estagio estagio);

    Optional<Relatorio> findTopByEstagioOrderByDataPrevistaEntregaDesc(Estagio estagio);

    @Query("SELECT r FROM Relatorio r WHERE r.dataEntregaRelatorio IS NULL AND r.dataPrevistaEntrega BETWEEN :dataInicio AND :dataFim")
    List<Relatorio> findAllPendentesComPrazoEntre(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    List<Relatorio> findByStatusAndDataPrevistaEntregaBefore(StatusRelatorio status, LocalDate data);

    List<Relatorio> findByStatus(StatusRelatorio status);

    @Query("SELECT r FROM Relatorio r WHERE r.status = :status AND r.estagio.orientador = :orientador")
    List<Relatorio> findByStatusAndEstagioOrientador(StatusRelatorio status, Professor orientador);
}
