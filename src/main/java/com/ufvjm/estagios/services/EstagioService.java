package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.EstagioCreateDTO;
import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstagioService {

    @Autowired
    private EstagioRepository estagioRepository;
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired RelatorioService relatorioService;

    @Transactional
    public Estagio criarEstagio(EstagioCreateDTO dto){

        Aluno aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        Professor orientador = professorRepository.findById(dto.orientadorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // Verifica se ja possui estágio com o status ativo ou em_analise
        List<StatusEstagio> statusAtivos = List.of(StatusEstagio.ATIVO, StatusEstagio.EM_ANALISE);

        boolean jaPossuiEstagio = estagioRepository.existsByAlunoAndStatusEstagioIn(aluno, statusAtivos);

        if (jaPossuiEstagio) {
            throw new RuntimeException("Este aluno já possui um estágio ativo ou em análise.");
        }

        Estagio novoEstagio = new Estagio();

        novoEstagio.setAluno(aluno);
        novoEstagio.setOrientador(orientador);
        novoEstagio.setConcedente(dto.concedente());
        novoEstagio.setSupervisor(dto.supervisor());
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

        Estagio estagioSalvo = estagioRepository.save(novoEstagio);

        relatorioService.gerarPrimeiroRelatorio(estagioSalvo);
        return estagioSalvo;
    }
}
