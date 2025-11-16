package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
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

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarPrazosDeRelatorios() {
        LocalDate prazo7dias = LocalDate.now().plusDays(7);
        LocalDate prazo15dias = LocalDate.now().plusDays(15);

        List<Relatorio> relatoriosVencendo = relatorioRepository.findAllPendentesComPrazoEntre(LocalDate.now(), prazo15dias);

        for (Relatorio relatorio : relatoriosVencendo) {
            long diasAteVencer = LocalDate.now().until(relatorio.getDataPrevistaEntrega(), java.time.temporal.ChronoUnit.DAYS);

            notificacaoService.criarNotificacao(relatorio.getEstagio().getAluno().getUsuario(), "Relatorio vence em " + diasAteVencer + " dias", "Seu relatório deve ser entregue até " + relatorio.getDataPrevistaEntrega(), TipoNotificacao.AVISO_PRAZO);
        }
    }
}
