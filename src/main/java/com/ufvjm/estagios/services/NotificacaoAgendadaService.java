package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.repositories.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoAgendadaService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private EstagioRepository estagioRepository;

    public void verificarPrazosDeRelatorios() {
        LocalDate prazo7dias = LocalDate.now().plusDays(7);
        LocalDate prazo15dias = LocalDate.now().plusDays(15);

        List<Relatorio> relatoriosVencendo = relatorioRepository.findAllPendentesComPrazoEntre(LocalDate.now(), prazo15dias);

        for (Relatorio relatorio : relatoriosVencendo) {
            long diasAteVencer = LocalDate.now().until(relatorio.getDataPrevistaEntrega(), java.time.temporal.ChronoUnit.DAYS);

            notificacaoService.criarNotificacao(relatorio.getEstagio().getAluno().getUsuario(), "Relatorio vence em " + diasAteVencer + " dias", "Seu relatório deve ser entregue até " + relatorio.getDataPrevistaEntrega(), TipoNotificacao.AVISO_PRAZO);
        }
    }

    private void verificarDocumentosPendentes() {
        // Busca estágios ativos que ainda não têm o TCE ou Plano entregues
        List<Estagio> estagiosComFalta = estagioRepository.findAllActiveWithMissingDocuments();

        for (Estagio estagio : estagiosComFalta) {
            // Verifica se a notificação já existe para não duplicar

            // Mensagem de Pendência de Documentos (O card Laranja)
            notificacaoService.criarNotificacao(
                    estagio.getAluno().getUsuario(),
                    "Documentação Pendente",
                    "Você precisa enviar os documentos pendentes (TCE e Plano de Atividades) para o estágio.",
                    TipoNotificacao.PENDENCIA
            );
        }
    }

    private void verificarRelatoriosAtrasados() {
        // 1. Busca relatórios com prazo no passado e status PENDENTE
        List<Relatorio> relatoriosAtrasados = relatorioRepository
                .findAllPendentesComPrazoEntre(LocalDate.MIN, LocalDate.now().minusDays(1)); // Do início dos tempos até ontem

        for (Relatorio relatorio : relatoriosAtrasados) {
            // 2. Cria a notificação de Atraso (O card Vermelho)
            notificacaoService.criarNotificacao(
                    relatorio.getEstagio().getAluno().getUsuario(),
                    "Prazo Vencido",
                    "O prazo para entrega do relatório foi ultrapassado. Status: " + relatorio.getStatus().name(),
                    TipoNotificacao.ATRASADO
            );
        }
    }

    @Scheduled(cron = "0 0 4 * * *") // Exemplo
    public void rotinaDiariaDeNotificacoes() {
        verificarPrazosDeRelatorios(); // (Aviso de 7 e 15 dias)
        verificarDocumentosPendentes(); // (Aviso de pendência Laranja)
        verificarRelatoriosAtrasados(); // (Aviso de vencimento Vermelho)
    }
}
