package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.*;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.*;
import com.ufvjm.estagios.repositories.AditivoRepository;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private AditivoRepository aditivoRepository;
    @Autowired
    private NotificacaoService notificacaoService;

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

        Estagio estagioSalvo = estagioRepository.save(novoEstagio);

        relatorioService.gerarPrimeiroRelatorio(estagioSalvo);

        notificacaoService.criarNotificacao(
                orientador.getUsuario(),
                "Novo Estágio Pendente",
                "O aluno " + aluno.getUsuario().getNome() + " submeteu um novo estágio para sua análise. Status: EM_ANALISE.",
                TipoNotificacao.PENDENCIA);

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

            String nomeProfessor = professorRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"))
                    .getUsuario().getNome();

            notificacaoService.criarNotificacao(
                    estagio.getAluno().getUsuario(),
                    "Estágio Aprovado",
                    "Seu estágio foi aprovado pelo Professor " + nomeProfessor + ".",
                    TipoNotificacao.APROVADO
            );
        } else {
            throw new RuntimeException("Estagio não está em analise");
        }
    }

    public List<Estagio> listarTodosEstagios(){
        return estagioRepository.findAllSortedByNextReportDate();
    }

    public EstagioResponseDTO getEstagioById(UUID estagioId, Usuario usuarioLogado){
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        if  (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            return converterParaDTO(estagio);
        }

        if  (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            return converterParaDTO(estagio);
        }

        if (usuarioLogado.getRole() == Role.ROLE_ALUNO) {
            Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            if (estagio.getAluno().equals(aluno)) {
                return converterParaDTO(estagio);
            }
        }

        throw new AccessDeniedException("Você não tem permissão para ver este estagio");
    }

    public List<EstagioResponseDTO> findEstagiosByAluno(Usuario usuarioLogado){
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        List<Estagio> estagios = estagioRepository.findByAluno(aluno);

        return estagios.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public List<Estagio> findEstagiosByProfessor(Usuario usuarioLogado){
        Professor professor = professorRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        return estagioRepository.findEstagiosByProfessorUsuarioSortedByNextReportDate(usuarioLogado);
    }

    @Transactional
    public Aditivo proporAditivo(UUID estagioId, AditivoCreateDTO dto) {
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        Period duracaoTotal = Period.between(estagio.getDataInicio(), dto.novaDataTermino());

        long diasDeEstagio = estagio.getDataInicio().until(dto.novaDataTermino(), java.time.temporal.ChronoUnit.DAYS);
        if (diasDeEstagio > (365 * 2)) {
            throw new RuntimeException("O estagio não pode exceder 2 anos");
        }

        Aditivo aditivo = new Aditivo();
        aditivo.setNovaDataTermino(dto.novaDataTermino());
        aditivo.setStatus(StatusAditivo.EM_ANALISE);
        aditivo.setEstagio(estagio);

        notificacaoService.criarNotificacao(
                estagio.getOrientador().getUsuario(), // Acessa o orientador do Estágio
                "Aditivo Pendente de Aprovação",
                "O aluno " + estagio.getAluno().getUsuario().getNome() + " submeteu uma proposta de Termo Aditivo para o estágio " + estagio.getId() + ".",
                TipoNotificacao.PENDENCIA
        );

        return aditivoRepository.save(aditivo);
    }

    @Transactional
    public Estagio concluirEstagio(UUID estagioId, Usuario usuarioLogado, ConclusaoPropostaDTO dto) {
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        verificaDonoDoEstagio(estagio, usuarioLogado);

        if (estagio.getStatusEstagio() != StatusEstagio.ATIVO) {
            throw new RuntimeException("O estágio precisa estar ativo para ser concluído.");
        }

        estagio.setEfetivado(dto.efetivado());
        estagio.setMotivoConclusao(dto.motivo());

        estagio.setStatusEstagio(StatusEstagio.ANALISE_CONCLUIDO);

        return estagioRepository.save(estagio);
    }

    @Transactional
    public void aprovarConclusao(UUID estagioId, Usuario usuarioLogado){

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

        if (estagio.getStatusEstagio() == StatusEstagio.ANALISE_CONCLUIDO) {
            estagio.setStatusEstagio(StatusEstagio.CONCLUIDO);

            estagioRepository.save(estagio);
        } else {
            throw new RuntimeException("Estagio não está em analise de conclusão");
        }

        notificacaoService.criarNotificacao(estagio.getAluno().getUsuario(), "Conclusão aprovada", "Sua conclusão foi aprovada", TipoNotificacao.APROVADO);
    }

    @Transactional
    public Estagio rescindirEstagio(UUID estagioId, Usuario usuarioLogado){
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        verificaDonoDoEstagio(estagio, usuarioLogado);

        estagio.setStatusEstagio(StatusEstagio.ANALISE_RESCINDIDO);

        return estagioRepository.save(estagio);
    }

    @Transactional
    public void aprovarRescisao(UUID estagioId, Usuario usuarioLogado){

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

        if (estagio.getStatusEstagio() == StatusEstagio.ANALISE_RESCINDIDO) {
            estagio.setStatusEstagio(StatusEstagio.RESCINDIDO);

            estagioRepository.save(estagio);
        } else {
            throw new RuntimeException("Estagio não está em analise de rescisão");
        }

        notificacaoService.criarNotificacao(estagio.getAluno().getUsuario(), "Recisão aprovada", "Sua recisão foi aprovada", TipoNotificacao.APROVADO);
    }

    private void verificaDonoDoEstagio(Estagio estagio, Usuario usuarioLogado){
        if (usuarioLogado.getRole() != Role.ROLE_ALUNO) {
            throw new AccessDeniedException("Apenas o aluno pode solicitar esta ação.");
        }

        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (!estagio.getAluno().equals(aluno)) {
            throw new AccessDeniedException("Você não tem permissão para alterar um estágio que não é seu.");
        }
    }

    @Transactional
    public Estagio atualizarEstagio(UUID id, EstagioUpdateDTO dto, Usuario usuarioLogado) {
        Estagio estagio = estagioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estagio não encontrado"));

        if (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            aplicarAtualizacaoCoordenador(estagio, dto);
        } else if (usuarioLogado.getRole() == Role.ROLE_ALUNO) {
            verificaDonoDoEstagio(estagio, usuarioLogado);

            aplicarAtualizacaoAluno(estagio, dto);
        } else {
            throw new AccessDeniedException("Usuario sem permissão para atualizar estagio");
        }

        return estagioRepository.save(estagio);
    }

    private void aplicarAtualizacaoCoordenador(Estagio estagio, EstagioUpdateDTO dto) {
        if (dto.orientadorId() != null){
            Professor orientador = professorRepository.findById(dto.orientadorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            estagio.setOrientador(orientador);
        }
        if (dto.concedente() != null && !dto.concedente().isBlank()) {
            estagio.setConcedente(dto.concedente());
        }
        if (dto.supervisor() != null && !dto.supervisor().isBlank()) {
            estagio.setSupervisor(dto.supervisor());
        }
        if (dto.formacaoSupervisor() != null && !dto.formacaoSupervisor().isBlank()) {
            estagio.setFormacaoSupervisor(dto.formacaoSupervisor());
        }
        if (dto.dataInicio() != null){
            estagio.setDataInicio(dto.dataInicio());
        }
        if (dto.dataTermino() != null){
            estagio.setDataTermino(dto.dataTermino());
        }
        if (dto.cargaHoraria() != null){
            estagio.setCargaHorariaSemanal(dto.cargaHoraria());
        }
        if (dto.valorBolsa() != null){
            estagio.setValorBolsa(dto.valorBolsa());
        }
        if (dto.auxilioTransporte() != null){
            estagio.setAuxilioTransporte(dto.auxilioTransporte());
        }
        if (dto.valorAuxilioTransporte() != null){
            estagio.setValorAuxilioTransporte(dto.valorAuxilioTransporte());
        }
        if (dto.seguro() != null){
            estagio.setSeguro(dto.seguro());
        }
        if (dto.dataEntregaTCE() != null){
            estagio.setDataEntregaTCE(dto.dataEntregaTCE());
        }
        if (dto.dataEntregaPlanoAtividade() != null){
            estagio.setDataEntregaPlanoDeAtividades(dto.dataEntregaPlanoAtividade());
        }
    }

    private void aplicarAtualizacaoAluno(Estagio estagio, EstagioUpdateDTO dto) {
        if (dto.concedente() != null && !dto.concedente().isBlank()) {
            estagio.setConcedente(dto.concedente());
        }
        if (dto.supervisor() != null && !dto.supervisor().isBlank()) {
            estagio.setSupervisor(dto.supervisor());
        }
        if (dto.formacaoSupervisor() != null && !dto.formacaoSupervisor().isBlank()) {
            estagio.setFormacaoSupervisor(dto.formacaoSupervisor());
        }
        if (dto.cargaHoraria() != null){
            estagio.setCargaHorariaSemanal(dto.cargaHoraria());
        }
        if (dto.auxilioTransporte() != null){
            estagio.setAuxilioTransporte(dto.auxilioTransporte());
        }
        if (dto.valorAuxilioTransporte() != null){
            estagio.setValorAuxilioTransporte(dto.valorAuxilioTransporte());
        }
        if (dto.seguro() != null){
            estagio.setSeguro(dto.seguro());
        }
        if (dto.dataEntregaTCE() != null){
            estagio.setDataEntregaTCE(dto.dataEntregaTCE());
        }
        if (dto.dataEntregaPlanoAtividade() != null){
            estagio.setDataEntregaPlanoDeAtividades(dto.dataEntregaPlanoAtividade());
        }
        if (estagio.getStatusEstagio() != StatusEstagio.EM_ANALISE) {
            estagio.setStatusEstagio(StatusEstagio.EM_ANALISE);
            estagio.setStatusEstagio(StatusEstagio.EM_ANALISE);
        }
    }

    @Transactional
    public void rejeitarEstagio(UUID estagioId, RejeicaoDTO dto, Usuario usuarioLogado) {
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

        estagio.setStatusEstagio(StatusEstagio.REJEITADO);
        estagioRepository.save(estagio);

        notificacaoService.criarNotificacao(estagio.getAluno().getUsuario(), "Estágio Rejeitado", "Motivo: " + dto.motivo(), TipoNotificacao.REJEITADO);
    }

    private RelatorioResponseDTO converterRelatorioParaDTO(Relatorio relatorio) {

        // Se estiver APROVADO, ele já está concluído.
        if (relatorio.getStatus() == StatusRelatorio.APROVADO) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    "Entregue", "verde"
            );
        }

        // Se estiver em ANÁLISE, ele está com o Professor.
        if (relatorio.getStatus() == StatusRelatorio.EM_ANALISE) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    "Em Análise", "azul"
            );
        }

        // Se estiver REJEITADO, é uma pendência vermelha.
        if (relatorio.getStatus() == StatusRelatorio.REJEITADO) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    "Rejeitado", "vermelho"
            );
        }

        long diasAteVencimento = ChronoUnit.DAYS.between(LocalDate.now(), relatorio.getDataPrevistaEntrega());
        if (diasAteVencimento < 0) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    "Atrasado", "vermelho"
            );
        } else if (diasAteVencimento <= 7) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    diasAteVencimento + " dias", "amarelo"
            );
        } else if (diasAteVencimento <= 15) {
            return new RelatorioResponseDTO(
                    relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                    diasAteVencimento + " dias", "laranja"
            );
        }

        // Default: Pendente, mas com prazo distante
        return new RelatorioResponseDTO(
                relatorio.getId(), "Relatório", relatorio.getDataPrevistaEntrega(), relatorio.getDataEntregaRelatorio(),
                "Pendente", "cinza"
        );
    }

    private AditivoResponseDTO converterAditivoParaDTO(Aditivo aditivo) {

        String descricao = "Aditivo - " + aditivo.getNovaDataTermino().toString();

        return new AditivoResponseDTO(
                aditivo.getId(),
                aditivo.getNovaDataTermino(),
                aditivo.getStatus(),
                descricao
        );
    }

    private EstagioResponseDTO converterParaDTO(Estagio estagio) {
        // Mapear e calcular o status de todos os relatórios
        List<RelatorioResponseDTO> relatoriosDTO = estagio.getRelatorios().stream()
                .map(this::converterRelatorioParaDTO)
                .toList();

        List<AditivoResponseDTO> aditivosDTO = estagio.getAditivos().stream()
                .map(this::converterAditivoParaDTO)
                .toList();

        // Retornar o EstagioResponseDTO com dados achatados
        return new EstagioResponseDTO(
                estagio.getId(),
                estagio.getStatusEstagio(),
                estagio.getAluno().getUsuario().getNome(),
                estagio.getOrientador().getUsuario().getNome(),
                estagio.getConcedente(),
                estagio.getSupervisor(),
                estagio.getFormacaoSupervisor(),
                estagio.getDataInicio(),
                estagio.getDataTermino(),
                estagio.getCargaHorariaSemanal(),
                estagio.getValorBolsa(),
                estagio.getAuxilioTransporte(),
                estagio.getValorAuxilioTransporte(),
                estagio.getSeguro(),
                estagio.getDataEntregaTCE(),
                estagio.getDataEntregaPlanoDeAtividades(),
                relatoriosDTO,
                aditivosDTO
        );
    }

    public List<EstagioResponseDTO> listarEstagiosDashboard(Usuario usuarioLogado) {
        List<Estagio> estagios = estagioRepository.findAllSortedByNextReportDate();

        return estagios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
}
