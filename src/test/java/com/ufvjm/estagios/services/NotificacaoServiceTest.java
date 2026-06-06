package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarNotificacaoSucesso() {
        String titulo = "Título da notificação";
        String descricao = "Descrição da notificação";
        Usuario destinatario = new Usuario();
        destinatario.setNome("Destinatário");
        destinatario.setRole(Role.ROLE_ALUNO);
        destinatario.setAtivo(true);

        UUID expectedId = UUID.fromString("00000000-0000-0000-0000-000000000123");
        when(notificacaoRepository.save(any(Notificacao.class)))
                .thenAnswer(invocation -> {
                    Notificacao n = invocation.getArgument(0);
                    n.setId(expectedId); // simulate DB-generated id (UUID)
                    return n;
                });

        notificacaoService.criarNotificacao(destinatario, titulo, descricao, TipoNotificacao.PENDENCIA);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        Notificacao salva = captor.getValue();
        assertNotNull(salva.getId());
        assertEquals(expectedId, salva.getId());
    }
}