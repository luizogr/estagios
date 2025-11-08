package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.EstagioCreateDTO;
import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
public class EstagioService {

    @Autowired
    private EstagioRepository estagioRepository;
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private RelatorioService relatorioService;

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

    @Transactional
    public void aprovarEstagio(UUID estagioId, Usuario usuarioLogado){

        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        boolean temPermissao = false;

        if (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            temPermissao = true;
        } else if (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));

            if(estagio.getOrientador().equals(professor)){
                temPermissao = true;
            }
        }
        if (!temPermissao) {
            throw new AccessDeniedException("Usuario sem permissão");
        }

        if (estagio.getStatusEstagio() == StatusEstagio.EM_ANALISE) {
            estagio.setStatusEstagio(StatusEstagio.ATIVO);

            estagioRepository.save(estagio);
        } else {
            throw new RuntimeException("Estagio não está em analise");
        }
    }

    public List<Estagio> listarTodosEstagios(){
        return estagioRepository.findAll();
    }

    public Estagio getEstagioById(UUID estagioId, Usuario usuarioLogado){
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        if  (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            return estagio;
        }

        if  (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            return estagio;
        }

        if (usuarioLogado.getRole() == Role.ROLE_ALUNO) {
            Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            if (estagio.getAluno().equals(aluno)) {
                return estagio;
            }
        }

        throw new AccessDeniedException("Você não tem permissão para ver este estagio");
    }

    public List<Estagio> findEstagiosByAluno(Usuario usuarioLogado){
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        return estagioRepository.findByAluno(aluno);
    }

    public List<Estagio> findEstagiosByProfessor(Usuario usuarioLogado){
        Professor professor = professorRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        return estagioRepository.findByOrientador(professor);
    }

}
