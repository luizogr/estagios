package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.StatusRelatorio;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.RelatorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificacaoAgendadaServiceTest {
    @Mock
    private RelatorioRepository relatorioRepository;

    @Mock
    private EstagioRepository estagioRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private NotificacaoAgendadaService notificacaoAgendadaService;

    private Usuario usuarioMock;
    private Aluno alunoMock;
    private Estagio estagioMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioMock = new Usuario();
        alunoMock = new Aluno();
        alunoMock.setUsuario(usuarioMock);

        estagioMock = new Estagio();
        estagioMock.setAluno(alunoMock);
    }

    @Test
    void deveExecutarRotinaDiariaEDispararTodasAsNotificacoesComSucesso() {
        Relatorio relatorioVencendo = new Relatorio();
        relatorioVencendo.setEstagio(estagioMock);
        relatorioVencendo.setDataPrevistaEntrega(LocalDate.now().plusDays(15));

        when(relatorioRepository.findAllPendentesComPrazoEntre(eq(LocalDate.now()), eq(LocalDate.now().plusDays(15))))
                .thenReturn(List.of(relatorioVencendo));

        when(estagioRepository.findAllActiveWithMissingDocuments())
                .thenReturn(List.of(estagioMock));

        Relatorio relatorioAtrasado = new Relatorio();
        relatorioAtrasado.setEstagio(estagioMock);
        relatorioAtrasado.setStatus(StatusRelatorio.PENDENTE);

        when(relatorioRepository.findAllPendentesComPrazoEntre(eq(LocalDate.MIN), eq(LocalDate.now().minusDays(1))))
                .thenReturn(List.of(relatorioAtrasado));

        notificacaoAgendadaService.rotinaDiariaDeNotificacoes();

        verify(notificacaoService).criarNotificacao(
                eq(usuarioMock),
                eq("Relatorio vence em 15 dias"),
                eq("Seu relatório deve ser entregue até " + relatorioVencendo.getDataPrevistaEntrega()),
                eq(TipoNotificacao.AVISO_PRAZO)
        );

        verify(notificacaoService).criarNotificacao(
                eq(usuarioMock),
                eq("Documentação Pendente"),
                eq("Você precisa enviar os documentos pendentes (TCE e Plano de Atividades) para o estágio."),
                eq(TipoNotificacao.PENDENCIA)
        );

        verify(notificacaoService).criarNotificacao(
                eq(usuarioMock),
                eq("Prazo Vencido"),
                eq("O prazo para entrega do relatório foi ultrapassado. Status: PENDENTE"),
                eq(TipoNotificacao.ATRASADO)
        );
    }
}