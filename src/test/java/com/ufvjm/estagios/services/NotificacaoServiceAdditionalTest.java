package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.MensagemDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;

class NotificacaoServiceAdditionalTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;
    @Mock
    private EstagioRepository estagioRepository;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enviarMensagem_parametrosInvalidos() {
        Usuario remetente = new Usuario();
        MensagemDTO dto = new MensagemDTO("t","d");

        assertThrows(IllegalArgumentException.class, () -> notificacaoService.enviarMensagemParaAluno(null, dto, remetente));
        assertThrows(IllegalArgumentException.class, () -> notificacaoService.enviarMensagemParaAluno(UUID.randomUUID(), null, remetente));
        assertThrows(NullPointerException.class, () -> notificacaoService.enviarMensagemParaAluno(UUID.randomUUID(), dto, null));
    }

    @Test
    void enviarMensagem_estagioNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(estagioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), new Usuario()));
    }

    @Test
    void enviarMensagem_alunoNaoAssociado() {
        UUID id = UUID.randomUUID();
        Estagio estagio = new Estagio();
        estagio.setId(id);
        estagio.setAluno(null);
        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        assertThrows(IllegalStateException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), new Usuario()));
    }

    @Test
    void enviarMensagem_alunoSemUsuario() {
        UUID id = UUID.randomUUID();
        Estagio estagio = new Estagio();
        Aluno aluno = new Aluno();
        aluno.setUsuario(null);
        estagio.setAluno(aluno);
        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        assertThrows(IllegalStateException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), new Usuario()));
    }

    @Test
    void enviarMensagem_coordenador_sucesso() {
        UUID id = UUID.randomUUID();
        Usuario usuarioAluno = new Usuario();
        usuarioAluno.setNome("Aluno");

        Aluno aluno = new Aluno();
        aluno.setUsuario(usuarioAluno);

        Estagio estagio = new Estagio();
        estagio.setId(id);
        estagio.setAluno(aluno);

        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        Usuario remetente = new Usuario();
        remetente.setRole(Role.ROLE_COORDENADOR);
        remetente.setId(UUID.randomUUID());

        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MensagemDTO dto = new MensagemDTO("Titulo","Descricao");
        notificacaoService.enviarMensagemParaAluno(id, dto, remetente);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        Notificacao salvo = captor.getValue();
        assertEquals("Titulo", salvo.getTitulo());
        assertEquals("Descricao", salvo.getDescricao());
        assertEquals(TipoNotificacao.MENSAGEM, salvo.getTipo());
        assertEquals(usuarioAluno, salvo.getDestinatario());
    }

    @Test
    void enviarMensagem_professorPerfilNaoEncontrado() {
        UUID id = UUID.randomUUID();
        Aluno aluno = new Aluno();
        Usuario usuarioAluno = new Usuario();
        aluno.setUsuario(usuarioAluno);
        Estagio estagio = new Estagio();
        estagio.setAluno(aluno);
        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        Usuario remetente = new Usuario();
        remetente.setRole(Role.ROLE_PROFESSOR);
        remetente.setId(UUID.randomUUID());

        when(professorRepository.findByUsuario(remetente)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), remetente));
    }

    @Test
    void enviarMensagem_professorNaoOrientador() {
        UUID id = UUID.randomUUID();
        Usuario usuarioAluno = new Usuario();
        Aluno aluno = new Aluno();
        aluno.setUsuario(usuarioAluno);

        Estagio estagio = new Estagio();
        estagio.setAluno(aluno);

        Professor orientador = new Professor();
        orientador.setId(UUID.randomUUID());
        estagio.setOrientador(orientador);

        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        Usuario remetente = new Usuario();
        remetente.setRole(Role.ROLE_PROFESSOR);
        remetente.setId(UUID.randomUUID());

        Professor professorLogado = new Professor();
        professorLogado.setId(UUID.randomUUID()); // different id -> not orientador

        when(professorRepository.findByUsuario(remetente)).thenReturn(Optional.of(professorLogado));

        assertThrows(AccessDeniedException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), remetente));
    }

    @Test
    void enviarMensagem_professorOrientador_sucesso() {
        UUID id = UUID.randomUUID();
        Usuario usuarioAluno = new Usuario();
        Aluno aluno = new Aluno();
        aluno.setUsuario(usuarioAluno);

        Estagio estagio = new Estagio();
        estagio.setAluno(aluno);

        Professor orientador = new Professor();
        orientador.setId(UUID.randomUUID());
        estagio.setOrientador(orientador);

        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        Usuario remetente = new Usuario();
        remetente.setRole(Role.ROLE_PROFESSOR);
        remetente.setId(UUID.randomUUID());

        // professorRepository must return the same professor instance as orientador for equality
        Professor professorLogado = new Professor();
        professorLogado.setId(orientador.getId());

        when(professorRepository.findByUsuario(remetente)).thenReturn(Optional.of(professorLogado));
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MensagemDTO dto = new MensagemDTO("T","D");
        notificacaoService.enviarMensagemParaAluno(id, dto, remetente);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        Notificacao salvo = captor.getValue();
        assertEquals(TipoNotificacao.MENSAGEM, salvo.getTipo());
    }

    @Test
    void enviarMensagem_alunoSemPermissao() {
        UUID id = UUID.randomUUID();
        Usuario usuarioAluno = new Usuario();
        Aluno aluno = new Aluno();
        aluno.setUsuario(usuarioAluno);
        Estagio estagio = new Estagio();
        estagio.setAluno(aluno);
        when(estagioRepository.findById(id)).thenReturn(Optional.of(estagio));

        Usuario remetente = new Usuario();
        remetente.setRole(Role.ROLE_ALUNO);

        assertThrows(AccessDeniedException.class, () -> notificacaoService.enviarMensagemParaAluno(id, new MensagemDTO("t","d"), remetente));
    }
}
