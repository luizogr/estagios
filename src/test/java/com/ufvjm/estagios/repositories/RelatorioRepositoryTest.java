package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.entities.enums.StatusRelatorio;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RelatorioRepositoryTest {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("Deve contar o número de relatórios de um estágio")
    void countByEstagio() {
        Estagio estagio = createEstagio();
        createRelatorio(estagio, LocalDate.now(), StatusRelatorio.PENDENTE);
        createRelatorio(estagio, LocalDate.now().plusMonths(1), StatusRelatorio.PENDENTE);

        long count = relatorioRepository.countByEstagio(estagio);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve buscar o último relatório ordenado pela data prevista de entrega")
    void findTopByEstagioOrderByDataPrevistaEntregaDesc() {
        Estagio estagio = createEstagio();
        LocalDate dataLonge = LocalDate.now().plusMonths(2);
        createRelatorio(estagio, LocalDate.now().plusMonths(1), StatusRelatorio.PENDENTE);
        createRelatorio(estagio, dataLonge, StatusRelatorio.PENDENTE);

        Optional<Relatorio> result = relatorioRepository.findTopByEstagioOrderByDataPrevistaEntregaDesc(estagio);

        assertThat(result).isPresent();
        assertThat(result.get().getDataPrevistaEntrega()).isEqualTo(dataLonge);
    }

    @Test
    @DisplayName("Deve buscar relatórios pendentes dentro de um intervalo de datas")
    void findAllPendentesComPrazoEntre() {
        Estagio estagio = createEstagio();
        LocalDate hoje = LocalDate.now();
        createRelatorio(estagio, hoje.plusDays(5), StatusRelatorio.PENDENTE);
        createRelatorio(estagio, hoje.plusDays(20), StatusRelatorio.PENDENTE);

        List<Relatorio> list = relatorioRepository.findAllPendentesComPrazoEntre(hoje, hoje.plusDays(10));

        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar relatórios por status e prazo vencido")
    void findByStatusAndDataPrevistaEntregaBefore() {
        Estagio estagio = createEstagio();
        createRelatorio(estagio, LocalDate.now().minusDays(1), StatusRelatorio.PENDENTE);

        List<Relatorio> list = relatorioRepository.findByStatusAndDataPrevistaEntregaBefore(StatusRelatorio.PENDENTE, LocalDate.now());

        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar relatórios por status")
    void findByStatus() {
        Estagio estagio = createEstagio();
        createRelatorio(estagio, LocalDate.now(), StatusRelatorio.EM_ANALISE);

        List<Relatorio> list = relatorioRepository.findByStatus(StatusRelatorio.EM_ANALISE);

        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar relatórios por status e orientador")
    void findByStatusAndEstagioOrientador() {
        Professor orientador = createProfessor();
        Estagio estagio = createEstagio(orientador);
        createRelatorio(estagio, LocalDate.now(), StatusRelatorio.EM_ANALISE);

        List<Relatorio> list = relatorioRepository.findByStatusAndEstagioOrientador(StatusRelatorio.EM_ANALISE, orientador);

        assertThat(list).hasSize(1);
    }

    private Professor createProfessor() {
        Professor professor = new Professor();
        professor.setSiap(UUID.randomUUID().toString().substring(0, 7));
        this.em.persist(professor);
        return professor;
    }

    private Aluno createAluno() {
        Aluno aluno = new Aluno();
        aluno.setMatricula(UUID.randomUUID().toString().substring(0, 10));
        this.em.persist(aluno);
        return aluno;
    }

    private Estagio createEstagio() {
        return createEstagio(createProfessor());
    }

    private Estagio createEstagio(Professor orientador) {
        Estagio estagio = new Estagio();
        estagio.setAluno(createAluno());
        estagio.setOrientador(orientador);
        estagio.setStatusEstagio(StatusEstagio.ATIVO);
        this.em.persist(estagio);
        return estagio;
    }

    private Relatorio createRelatorio(Estagio estagio, LocalDate dataPrevista, StatusRelatorio status) {
        Relatorio relatorio = new Relatorio();
        relatorio.setEstagio(estagio);
        relatorio.setDataPrevistaEntrega(dataPrevista);
        relatorio.setStatus(status);
        if (status == StatusRelatorio.EM_ANALISE) {
            relatorio.setDataEntregaRelatorio(LocalDate.now());
        }
        this.em.persist(relatorio);
        return relatorio;
    }
}
