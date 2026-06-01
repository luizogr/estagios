package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.dto.AditivoCreateDTO;
import com.ufvjm.estagios.dto.AlunoRegisterDTO;
import com.ufvjm.estagios.dto.EstagioCreateDTO;
import com.ufvjm.estagios.dto.ProfessorRegisterDTO;
import com.ufvjm.estagios.entities.Aditivo;
import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AditivoRepositoryTest {

    @Autowired
    AditivoRepository aditivoRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Sucesso ao buscar aditios de um determinado professor")
    void findByStatusAndEstagioOrientadorCase1() {
        AlunoRegisterDTO alunoRegisterDTO = new AlunoRegisterDTO("Luiz", "email@ufvjm.edu.br", "123456", "12345678901");
        Aluno aluno = this.createAluno(alunoRegisterDTO);
        ProfessorRegisterDTO professorRegisterDTO = new ProfessorRegisterDTO("P1", "p1@ufvjm.edu.br", "123456", "1234567");
        Professor professor = this.createProfessor(professorRegisterDTO);
        EstagioCreateDTO estagioCreateDTO = new EstagioCreateDTO(aluno.getId(), professor.getId(), "teste", "testesuperv", "Ti", LocalDate.now(), LocalDate.now(), 10, new BigDecimal(1000), false, new BigDecimal(0), false, LocalDate.now(), LocalDate.now());
        Estagio estagio = createEstagio(aluno, professor, estagioCreateDTO);
        AditivoCreateDTO aditivoCreateDTO = new AditivoCreateDTO(LocalDate.now());
        createAditivo(estagio, aditivoCreateDTO);

        List<Aditivo> list = this.aditivoRepository.findByStatusAndEstagioOrientador(StatusAditivo.EM_ANALISE, professor);

        assertThat(list.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("Erro ao buscar aditios de um determinado professor")
    void findByStatusAndEstagioOrientadorCase2() {
        ProfessorRegisterDTO professorRegisterDTO = new ProfessorRegisterDTO("P1", "p1@ufvjm.edu.br", "123456", "1234567");
        Professor professor = this.createProfessor(professorRegisterDTO);

        List<Aditivo> list = this.aditivoRepository.findByStatusAndEstagioOrientador(StatusAditivo.EM_ANALISE, professor);

        assertThat(list.isEmpty()).isTrue();

    }

    private Aluno createAluno(AlunoRegisterDTO alunoRegisterDTO) {
        Aluno aluno = new Aluno();
        aluno.setMatricula(alunoRegisterDTO.matricula());
        this.em.persist(aluno);
        return aluno;
    }

    private Professor createProfessor(ProfessorRegisterDTO professorRegisterDTO) {
        Professor professor = new Professor();
        professor.setSiap(professorRegisterDTO.siap());
        this.em.persist(professor);
        return professor;
    }

    private Estagio createEstagio(Aluno aluno, Professor orientador, EstagioCreateDTO  dto) {
        Estagio novoEstagio = new Estagio();

        novoEstagio.setAluno(aluno);
        novoEstagio.setOrientador(orientador);
        novoEstagio.setConcedente(dto.concedente());
        novoEstagio.setSupervisor(dto.supervisor());
        novoEstagio.setFormacaoSupervisor(dto.formacaoSupervisor());
        novoEstagio.setDataInicio(dto.dataInicio());
        novoEstagio.setDataTermino(dto.dataTermino());
        novoEstagio.setCargaHorariaSemanal(dto.cargaHoraria());
        novoEstagio.setValorBolsa(dto.valorBolsa());
        novoEstagio.setAuxilioTransporte(dto.auxilioTransporte());
        novoEstagio.setValorAuxilioTransporte(dto.valorAuxilioTransporte());
        novoEstagio.setSeguro(dto.seguro());
        novoEstagio.setDataEntregaTCE(dto.dataEntregaTCE());
        novoEstagio.setDataEntregaPlanoDeAtividades(dto.dataEntregaPlanoAtividade());

        novoEstagio.setStatusEstagio(StatusEstagio.EM_ANALISE);
        this.em.persist(novoEstagio);

        return novoEstagio;
    }

    private Aditivo createAditivo(Estagio estagio, AditivoCreateDTO aditivoCreateDTO) {
        Aditivo aditivo = new Aditivo();
        aditivo.setEstagio(estagio);
        aditivo.setNovaDataTermino(aditivoCreateDTO.novaDataTermino());
        aditivo.setStatus(StatusAditivo.EM_ANALISE);
        this.em.persist(aditivo);
        return aditivo;
    }
}
