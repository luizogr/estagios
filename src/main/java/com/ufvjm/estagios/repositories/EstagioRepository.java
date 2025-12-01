package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstagioRepository extends JpaRepository<Estagio, UUID> {

    boolean existsByAlunoAndStatusEstagioIn(Aluno aluno, List<StatusEstagio> statuses);

    List<Estagio> findByAluno(Aluno aluno);

    List<Estagio> findByOrientador(Professor professor);

    List<Estagio> findByStatusEstagio(StatusEstagio status);

    List<Estagio> findByOrientadorAndStatusEstagioIn(
            Professor orientador,
            List<StatusEstagio> statuses // Ex: [ATIVO, EM_ANALISE]
    );

    @Query("SELECT e FROM Estagio e " +
            "JOIN e.orientador p " +
            "LEFT JOIN Relatorio r ON r.estagio.id = e.id AND r.dataEntregaRelatorio IS NULL " +
            "WHERE p.usuario = :usuarioLogado " +
            "GROUP BY e " + // <--- ADICIONADO O GROUP BY AQUI
            "ORDER BY MIN(r.dataPrevistaEntrega) ASC") // Ordena pela menor (mais próxima) data prevista entre os relatórios pendentes
    List<Estagio> findEstagiosByProfessorUsuarioSortedByNextReportDate(@Param("usuarioLogado") Usuario usuarioLogado);

    @Query("SELECT e FROM Estagio e " +
            "LEFT JOIN e.relatorios r " +
            "WHERE r.status = 'PENDENTE' OR r.status = 'EM_ANALISE' " + // Considera relatórios não finalizados
            "GROUP BY e " +
            "ORDER BY MIN(r.dataPrevistaEntrega) ASC")
    List<Estagio> findAllSortedByNextReportDate();

    @Query("SELECT e FROM Estagio e WHERE e.statusEstagio = 'ATIVO' AND (e.dataEntregaTCE IS NULL OR e.dataEntregaPlanoDeAtividades IS NULL)")
    List<Estagio> findAllActiveWithMissingDocuments();
}
