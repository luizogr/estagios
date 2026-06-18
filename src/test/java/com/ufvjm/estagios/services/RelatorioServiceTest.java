package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import com.ufvjm.estagios.entities.enums.StatusRelatorio;
import com.ufvjm.estagios.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private RelatorioRepository relatorioRepository;
    @Mock
    private EstagioRepository estagioRepository;
    @Mock
    private AditivoRepository aditivoRepository;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private NotificacaoService notificacaoService;
    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private Estagio estagio;
    private Usuario alunoUsuario, professorUsuario, coordenadorUsuario;
    private Aluno aluno;
    private Professor professor;
    private Relatorio relatorio;
    private UUID relatorioId;

    @BeforeEach
    void setUp() {
        relatorioId = UUID.randomUUID();

        alunoUsuario = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000123"), "Aluno", "aluno@ufvjm.edu.br", "senha", Role.ROLE_ALUNO);
        professorUsuario = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000321"), "Professor", "professor@ufvjm.edu.br", "senha", Role.ROLE_PROFESSOR);
        coordenadorUsuario = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000213"), "Coordenador", "coordenador@ufvjm.edu.br", "senha", Role.ROLE_COORDENADOR);
        alunoUsuario.setAtivo(true);
        professorUsuario.setAtivo(true);
        coordenadorUsuario.setAtivo(true);

        aluno = new Aluno();
        aluno.setUsuario(alunoUsuario);

        professor = new Professor();
        professor.setUsuario(professorUsuario);

        estagio = new Estagio();
        estagio.setId(UUID.fromString("00000000-0000-0000-0000-000000000111"));
        estagio.setAluno(aluno);
        estagio.setOrientador(professor);
        estagio.setDataInicio(LocalDate.now().minusMonths(1));
        estagio.setDataTermino(LocalDate.now().plusMonths(5));

        relatorio = new Relatorio();
        relatorio.setId(relatorioId);
        relatorio.setEstagio(estagio);
    }

    @Test
    void gerarPrimeiroRelatorio_Success() {
        ArgumentCaptor<Relatorio> relatorioCaptor = ArgumentCaptor.forClass(Relatorio.class);

        relatorioService.gerarPrimeiroRelatorio(estagio);

        verify(relatorioRepository).save(relatorioCaptor.capture());
        Relatorio captured = relatorioCaptor.getValue();

        assertEquals(estagio, captured.getEstagio());
        assertEquals(StatusRelatorio.PENDENTE, captured.getStatus());
        assertEquals(estagio.getDataInicio().plusMonths(6), captured.getDataPrevistaEntrega());
    }

    @Test
    void getDataFim_WithAditivo() {
        LocalDate novaDataFim = estagio.getDataTermino().plusMonths(6);
        Aditivo aditivo = new Aditivo();
        aditivo.setNovaDataTermino(novaDataFim);

        when(aditivoRepository.findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(estagio, StatusAditivo.APROVADO))
                .thenReturn(Optional.of(aditivo));

        LocalDate dataFim = relatorioService.getDataFim(estagio);

        assertEquals(novaDataFim, dataFim);
    }

    @Test
    void getDataFim_WithoutAditivo() {
        when(aditivoRepository.findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(estagio, StatusAditivo.APROVADO))
                .thenReturn(Optional.empty());

        LocalDate dataFim = relatorioService.getDataFim(estagio);

        assertEquals(estagio.getDataTermino(), dataFim);
    }

    @Test
    void rejeitarRelatorio_Success_AsCoordenador() {
        relatorio.setStatus(StatusRelatorio.EM_ANALISE);
        RejeicaoDTO dto = new RejeicaoDTO("Motivo do teste");
        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));

        relatorioService.rejeitarRelatorio(relatorioId, dto, coordenadorUsuario);

        assertEquals(StatusRelatorio.REJEITADO, relatorio.getStatus());
        verify(relatorioRepository).save(relatorio);
        verify(notificacaoService).criarNotificacao(any(), any(), contains("Motivo do teste"), any());
    }

    /*@Test
    void rejeitarRelatorio_AccessDenied() {
        relatorio.setStatus(StatusRelatorio.EM_ANALISE);
        RejeicaoDTO dto = new RejeicaoDTO("Motivo");
        Usuario outroProfessorUsuario = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000222"), "Outro Prof", "outro@ufvjm.edu.br", "senha", Role.ROLE_PROFESSOR);
        Professor outroProfessor = new Professor();
        outroProfessor.setUsuario(outroProfessorUsuario);

        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));
        when(professorRepository.findByUsuario(outroProfessorUsuario)).thenReturn(Optional.of(outroProfessor));

        assertThrows(AccessDeniedException.class, () -> {
            relatorioService.rejeitarRelatorio(relatorioId, dto, outroProfessorUsuario);
        });
    }*/

    @Test
    void aprovarRelatorio_Success_AsOrientador() {
        relatorio.setStatus(StatusRelatorio.EM_ANALISE);
        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));
        when(professorRepository.findByUsuario(professorUsuario)).thenReturn(Optional.of(professor));

        relatorioService.aprovarRelatorio(relatorioId, professorUsuario);

        assertEquals(StatusRelatorio.APROVADO, relatorio.getStatus());
        verify(relatorioRepository).save(relatorio);
        verify(notificacaoService).criarNotificacao(any(), eq("Relatório Aprovado"), any(), any());
    }

    @Test
    void aprovarRelatorio_InvalidStatus() {
        relatorio.setStatus(StatusRelatorio.PENDENTE); // Not EM_ANALISE
        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));

        assertThrows(RuntimeException.class, () -> {
            relatorioService.aprovarRelatorio(relatorioId, coordenadorUsuario);
        });
    }

    @Test
    void entregarRelatorio_Success() {
        relatorio.setStatus(StatusRelatorio.PENDENTE);
        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));
        when(alunoRepository.findByUsuario(alunoUsuario)).thenReturn(Optional.of(aluno));

        relatorioService.entregarRelatorio(relatorioId, alunoUsuario);

        assertEquals(StatusRelatorio.EM_ANALISE, relatorio.getStatus());
        assertNotNull(relatorio.getDataEntregaRelatorio());
        verify(relatorioRepository).save(relatorio);
        verify(notificacaoService).criarNotificacao(eq(professorUsuario), any(), any(), any());
    }

    /*@Test
    void entregarRelatorio_NotOwner() {
        Usuario outroAlunoUsuario = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000333"), "Outro Aluno", "outro@ufvjm.edu.br", "senha", Role.ROLE_ALUNO);
        Aluno outroAluno = new Aluno();
        outroAluno.setUsuario(outroAlunoUsuario);

        relatorio.setStatus(StatusRelatorio.PENDENTE);
        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));
        when(alunoRepository.findByUsuario(outroAlunoUsuario)).thenReturn(Optional.of(outroAluno));

        assertThrows(AccessDeniedException.class, () -> {
            relatorioService.entregarRelatorio(relatorioId, outroAlunoUsuario);
        });
    }*/
}