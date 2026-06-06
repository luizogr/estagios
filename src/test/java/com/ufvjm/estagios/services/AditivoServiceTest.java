package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.AditivoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AditivoServiceTest {

    @Mock
    private AditivoRepository aditivoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private AditivoService aditivoService;

    private UUID aditivoId;
    private Usuario coordenador;
    private Usuario professorUsuario;
    private Professor professor;
    private Aditivo aditivo;
    private Estagio estagio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        aditivoId = UUID.randomUUID();
        coordenador = new Usuario();
        coordenador.setRole(Role.ROLE_COORDENADOR);

        professorUsuario = new Usuario();
        professorUsuario.setRole(Role.ROLE_PROFESSOR);

        professor = new Professor();
        professor.setUsuario(professorUsuario);

        Aluno aluno = new Aluno();
        aluno.setUsuario(new Usuario());

        estagio = new Estagio();
        estagio.setOrientador(professor);
        estagio.setAluno(aluno);

        aditivo = new Aditivo();
        aditivo.setId(aditivoId);
        aditivo.setEstagio(estagio);
        aditivo.setStatus(StatusAditivo.EM_ANALISE);
        aditivo.setNovaDataTermino(LocalDate.now().plusMonths(6));
    }

    @Test
    void aprovarAditivo_SucessoCoordenador() {
        when(aditivoRepository.findById(aditivoId)).thenReturn(Optional.of(aditivo));

        aditivoService.aprovarAditivo(aditivoId, coordenador);

        assertEquals(StatusAditivo.APROVADO, aditivo.getStatus());
        verify(aditivoRepository, times(1)).save(aditivo);
        verify(notificacaoService, times(1)).criarNotificacao(any(), any(), any(), eq(TipoNotificacao.PRORROGACAO));
    }

    @Test
    void aprovarAditivo_SucessoProfessorOrientador() {
        when(aditivoRepository.findById(aditivoId)).thenReturn(Optional.of(aditivo));
        when(professorRepository.findByUsuario(professorUsuario)).thenReturn(Optional.of(professor));

        aditivoService.aprovarAditivo(aditivoId, professorUsuario);

        assertEquals(StatusAditivo.APROVADO, aditivo.getStatus());
        verify(aditivoRepository, times(1)).save(aditivo);
    }

    @Test
    void aprovarAditivo_ErroSemPermissao() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setRole(Role.ROLE_ALUNO);
        when(aditivoRepository.findById(aditivoId)).thenReturn(Optional.of(aditivo));

        assertThrows(AccessDeniedException.class, () -> {
            aditivoService.aprovarAditivo(aditivoId, outroUsuario);
        });
    }

    @Test
    void rejeitarAditivo_SucessoCoordenador() {
        RejeicaoDTO dto = new RejeicaoDTO("Documentação incompleta");
        when(aditivoRepository.findById(aditivoId)).thenReturn(Optional.of(aditivo));

        aditivoService.rejeitarAditivo(aditivoId, dto, coordenador);

        assertEquals(StatusAditivo.REJEITADO, aditivo.getStatus());
        verify(aditivoRepository, times(1)).save(aditivo);
        verify(notificacaoService, times(1)).criarNotificacao(any(), any(), contains("Motivo: Documentação incompleta"), eq(TipoNotificacao.REJEITADO));
    }

    @Test
    void rejeitarAditivo_ErroAditivoNaoEmAnalise() {
        aditivo.setStatus(StatusAditivo.APROVADO);
        RejeicaoDTO dto = new RejeicaoDTO("Motivo");
        when(aditivoRepository.findById(aditivoId)).thenReturn(Optional.of(aditivo));

        assertThrows(RuntimeException.class, () -> {
            aditivoService.rejeitarAditivo(aditivoId, dto, coordenador);
        });
    }
}
