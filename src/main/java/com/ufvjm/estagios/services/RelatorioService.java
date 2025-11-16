package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.*;
import com.ufvjm.estagios.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private EstagioRepository estagioRepository;
    @Autowired
    private AditivoRepository aditivoRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private AlunoRepository alunoRepository;

    public void gerarPrimeiroRelatorio(Estagio estagio) {

        Relatorio primeiroRelatorio = new Relatorio();
        primeiroRelatorio.setEstagio(estagio);
        primeiroRelatorio.setStatus(StatusRelatorio.PENDENTE);

        LocalDate prazo = estagio.getDataInicio().plusMonths(6);
        primeiroRelatorio.setDataPrevistaEntrega(prazo);

        relatorioRepository.save(primeiroRelatorio);
    }

    @Transactional
    public void sincronizarRelatoriosPendentes() {
        List<Estagio> estagiosAtivos = estagioRepository.findByStatusEstagio(StatusEstagio.ATIVO);
        for (Estagio estagio : estagiosAtivos) {
            LocalDate dataFim = getDataFim(estagio);
            LocalDate dataInicio = estagio.getDataInicio();

            long mesesDesdeInicio = ChronoUnit.MONTHS.between(dataInicio, LocalDate.now());
            int relatoriosNecessarios = (int) (mesesDesdeInicio / 6) + 1;

            long relatoriosExistentes = relatorioRepository.countByEstagio(estagio);

            for (long i = relatoriosExistentes + 1; i <= relatoriosNecessarios; i++) {
                LocalDate dataPrazoEntrega = dataInicio.plusMonths(i * 6);

                if (!dataPrazoEntrega.isAfter(dataFim)) {
                    Relatorio relatorio = new Relatorio();
                    relatorio.setEstagio(estagio);
                    relatorio.setDataPrevistaEntrega(dataPrazoEntrega);
                    relatorio.setStatus(StatusRelatorio.PENDENTE);

                    relatorioRepository.save(relatorio);
                } else {
                    break;
                }

            }
        }
    }

    public LocalDate getDataFim(Estagio estagio) {
        Optional<Aditivo> ultimoAditivoAprovado = aditivoRepository
                .findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(estagio, StatusAditivo.APROVADO);
        if (ultimoAditivoAprovado.isPresent()) {
            return ultimoAditivoAprovado.get().getNovaDataTermino();
        } else {
            return estagio.getDataTermino();
        }
    }

    @Transactional
    public void rejeitarRelatorio(UUID relatorioId, RejeicaoDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new RuntimeException("Relatorio não encontrado"));

        boolean temPermissao = false;
        if (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            temPermissao = true;
        } else if (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            if (relatorio.getEstagio().getOrientador().equals(professor)) {
                temPermissao = true;
            }
        }
        if (!temPermissao) {
            throw new AccessDeniedException("Usuario nao permitido");
        }

        if (relatorio.getStatus() == StatusRelatorio.EM_ANALISE) {
            relatorio.setStatus(StatusRelatorio.REJEITADO);
            relatorioRepository.save(relatorio);

            // 3. Envia a notificação com o motivo
            notificacaoService.criarNotificacao(
                    relatorio.getEstagio().getAluno().getUsuario(),
                    "Relatório Rejeitado",
                    "Motivo: " + dto.motivo(),
                    TipoNotificacao.REJEITADO
            );
        } else {
            throw new RuntimeException("Este relatório não está mais pendente.");
        }
    }

    @Transactional
    public void aprovarRelatorio(UUID relatorioId, @AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new RuntimeException("Relatorio não encontrado"));

        boolean temPermissao = false;
        if (usuarioLogado.getRole() == Role.ROLE_COORDENADOR) {
            temPermissao = true;
        } else if (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            if (relatorio.getEstagio().getOrientador().equals(professor)) {
                temPermissao = true;
            }
        }
        if (!temPermissao) {
            throw new AccessDeniedException("Usuario nao permitido");
        }

        if (relatorio.getStatus() == StatusRelatorio.EM_ANALISE) {
            relatorio.setStatus(StatusRelatorio.APROVADO);
            relatorioRepository.save(relatorio);

            // 3. Envia a notificação com o motivo
            notificacaoService.criarNotificacao(
                    relatorio.getEstagio().getAluno().getUsuario(),
                    "Relatório Aprovado",
                    "Relatorio aprovado",
                    TipoNotificacao.APROVADO
            );
        } else {
            throw new RuntimeException("Este relatório não está mais pendente.");
        }
    }

    @Transactional
    public void entregarRelatorio(UUID relatorioId, Usuario usuarioLogado) {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        verificarDonoDoRelatorio(relatorio, usuarioLogado);

        if (relatorio.getStatus() == StatusRelatorio.PENDENTE || relatorio.getStatus() == StatusRelatorio.REJEITADO) {
            relatorio.setDataEntregaRelatorio(LocalDate.now());
            relatorio.setStatus(StatusRelatorio.EM_ANALISE);
            relatorioRepository.save(relatorio);

            notificacaoService.criarNotificacao(
                    relatorio.getEstagio().getOrientador().getUsuario(),
                    "Relatório Entregue para Análise",
                    "O aluno " + usuarioLogado.getNome() + " entregou o relatório.",
                    TipoNotificacao.DOCUMENTO
            );
        } else {
            throw new RuntimeException("Este relatório não está mais pendente ou rejeitado.");
        }
    }

    private void verificarDonoDoRelatorio(Relatorio relatorio, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() != Role.ROLE_ALUNO) {
            throw new AccessDeniedException("Apenas o aluno pode entregar o relatório.");
        }
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Perfil de Aluno não encontrado"));

        if (!relatorio.getEstagio().getAluno().equals(aluno)) {
            throw new AccessDeniedException("Você não tem permissão para entregar este relatório.");
        }
    }
}