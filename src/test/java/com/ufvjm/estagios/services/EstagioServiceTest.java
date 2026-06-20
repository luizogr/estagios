package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.*;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.*;
import com.ufvjm.estagios.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstagioServiceTest {

    @Mock
    private EstagioRepository estagioRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private RelatorioService relatorioService;

    @Mock
    private AditivoRepository aditivoRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private RelatorioRepository relatorioRepository;

    @InjectMocks
    private EstagioService estagioService;

    private Usuario alunoUsuario;
    private Aluno aluno;
    private Usuario professorUsuario;
    private Professor professor;
    private Estagio estagio;

    @BeforeEach
    void setUp() {
        alunoUsuario = new Usuario();
        alunoUsuario.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        alunoUsuario.setNome("Aluno Teste");
        alunoUsuario.setEmailInstitucional("aluno@ufvjm.edu.br");
        alunoUsuario.setRole(Role.ROLE_ALUNO);

        aluno = new Aluno();
        aluno.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        aluno.setMatricula("2021001");
        aluno.setUsuario(alunoUsuario);

        professorUsuario = new Usuario();
        professorUsuario.setId(UUID.fromString("00000000-0000-0000-0000-000000000010"));
        professorUsuario.setNome("Professor Teste");
        professorUsuario.setEmailInstitucional("prof@ufvjm.edu.br");
        professorUsuario.setRole(Role.ROLE_PROFESSOR);

        professor = new Professor();
        professor.setId(UUID.fromString("00000000-0000-0000-0000-000000000011"));
        professor.setSiap("12345");
        professor.setUsuario(professorUsuario);

        estagio = new Estagio();
        estagio.setId(UUID.fromString("00000000-0000-0000-0000-000000000100"));
        estagio.setAluno(aluno);
        estagio.setOrientador(professor);
        estagio.setDataInicio(LocalDate.now().minusMonths(6));
        estagio.setDataTermino(LocalDate.now().plusMonths(6));
        estagio.setStatusEstagio(StatusEstagio.EM_ANALISE);
    }

    @Test
    void criarEstagio_Success() {
        EstagioCreateDTO dto = new EstagioCreateDTO(
                aluno.getId(),
                professor.getId(),
                "Empresa X",
                "Supervisor X",
                "Formação",
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                20,
                new BigDecimal(1000),
                false,
                new BigDecimal(0),
                false,
                LocalDate.now(),
                LocalDate.now()
        );

        when(alunoRepository.findById(dto.alunoId())).thenReturn(Optional.of(aluno));
        when(professorRepository.findById(dto.orientadorId())).thenReturn(Optional.of(professor));
        when(estagioRepository.existsByAlunoAndStatusEstagioIn(any(Aluno.class), anyList())).thenReturn(false);
        when(estagioRepository.save(any(Estagio.class))).thenAnswer(i -> i.getArgument(0));

        Estagio result = estagioService.criarEstagio(dto);

        assertNotNull(result);
        assertEquals(StatusEstagio.EM_ANALISE, result.getStatusEstagio());
        verify(notificacaoService, times(1)).criarNotificacao(any(Usuario.class), anyString(), anyString(), any(TipoNotificacao.class));
    }

    @Test
    void aprovarEstagio_ByProfessor_Success() {
        estagio.setStatusEstagio(StatusEstagio.EM_ANALISE);

        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(professorRepository.findByUsuario(professorUsuario)).thenReturn(Optional.of(professor));

        estagioService.aprovarEstagio(estagio.getId(), professorUsuario);

        assertEquals(StatusEstagio.ATIVO, estagio.getStatusEstagio());
        verify(estagioRepository, times(1)).save(estagio);
        verify(relatorioService, times(1)).sincronizarRelatoriosPendentes();
        verify(notificacaoService, times(1)).criarNotificacao(any(Usuario.class), anyString(), anyString(), any(TipoNotificacao.class));
    }

    @Test
    void getEstagioById_AsAluno_Success() {
        estagio.setStatusEstagio(StatusEstagio.ATIVO);
        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(alunoRepository.findByUsuario(alunoUsuario)).thenReturn(Optional.of(aluno));

        EstagioResponseDTO dto = estagioService.getEstagioById(estagio.getId(), alunoUsuario);

        assertNotNull(dto);
        assertEquals(estagio.getId(), dto.id());
    }

    @Test
    void getEstagioById_AccessDenied_WhenNotOwner() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(UUID.randomUUID());
        outroUsuario.setRole(Role.ROLE_ALUNO);

        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(alunoRepository.findByUsuario(outroUsuario)).thenReturn(Optional.of(new Aluno()));

        assertThrows(AccessDeniedException.class, () -> estagioService.getEstagioById(estagio.getId(), outroUsuario));
    }

    @Test
    void findEstagiosByAluno_ReturnsList() {
        when(alunoRepository.findByUsuario(alunoUsuario)).thenReturn(Optional.of(aluno));
        when(estagioRepository.findByAluno(aluno)).thenReturn(List.of(estagio));

        List<EstagioResponseDTO> result = estagioService.findEstagiosByAluno(alunoUsuario);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(estagio.getId(), result.get(0).id());
    }

    @Test
    void proporAditivo_Success() {
        AditivoCreateDTO dto = new AditivoCreateDTO(LocalDate.now().plusMonths(3));
        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(aditivoRepository.save(any(Aditivo.class))).thenAnswer(i -> i.getArgument(0));

        Aditivo result = estagioService.proporAditivo(estagio.getId(), dto);

        assertNotNull(result);
        assertEquals(StatusAditivo.EM_ANALISE, result.getStatus());
        assertEquals(estagio, result.getEstagio());
        verify(notificacaoService, times(1)).criarNotificacao(any(Usuario.class), anyString(), anyString(), any(TipoNotificacao.class));
    }

    @Test
    void concluirEstagio_ByAluno_Success() {
        estagio.setStatusEstagio(StatusEstagio.ATIVO);
        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(alunoRepository.findByUsuario(alunoUsuario)).thenReturn(Optional.of(aluno));
        when(estagioRepository.save(any(Estagio.class))).thenAnswer(i -> i.getArgument(0));

        ConclusaoPropostaDTO dto = new ConclusaoPropostaDTO(true, "Concluído");
        Estagio result = estagioService.concluirEstagio(estagio.getId(), alunoUsuario, dto);

        assertNotNull(result);
        assertEquals(StatusEstagio.ANALISE_CONCLUIDO, result.getStatusEstagio());
    }

    @Test
    void aprovarConclusao_ByProfessor_Success() {
        estagio.setStatusEstagio(StatusEstagio.ANALISE_CONCLUIDO);
        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(professorRepository.findByUsuario(professorUsuario)).thenReturn(Optional.of(professor));

        estagioService.aprovarConclusao(estagio.getId(), professorUsuario);

        assertEquals(StatusEstagio.CONCLUIDO, estagio.getStatusEstagio());
        verify(estagioRepository, times(1)).save(estagio);
        verify(notificacaoService, times(1)).criarNotificacao(any(Usuario.class), anyString(), anyString(), any(TipoNotificacao.class));
    }

    @Test
    void atualizarEstagio_Coordenador_CanChangeOrientador() {
        Usuario coordenador = new Usuario();
        coordenador.setRole(Role.ROLE_COORDENADOR);

        Professor novoProfessor = new Professor();
        novoProfessor.setId(UUID.fromString("00000000-0000-0000-0000-000000000200"));

        EstagioUpdateDTO dto = new EstagioUpdateDTO(novoProfessor.getId(), null, null, null, null, null, null, null, null, null, null, null, null);

        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(professorRepository.findById(novoProfessor.getId())).thenReturn(Optional.of(novoProfessor));
        when(estagioRepository.save(any(Estagio.class))).thenAnswer(i -> i.getArgument(0));

        Estagio result = estagioService.atualizarEstagio(estagio.getId(), dto, coordenador);

        assertNotNull(result);
        assertEquals(novoProfessor, result.getOrientador());
    }

    @Test
    void rejeitarEstagio_ByProfessor_Success() {
        estagio.setStatusEstagio(StatusEstagio.EM_ANALISE);
        when(estagioRepository.findById(estagio.getId())).thenReturn(Optional.of(estagio));
        when(professorRepository.findByUsuario(professorUsuario)).thenReturn(Optional.of(professor));

        RejeicaoDTO dto = new RejeicaoDTO("Motivo XYZ");
        estagioService.rejeitarEstagio(estagio.getId(), dto, professorUsuario);

        assertEquals(StatusEstagio.REJEITADO, estagio.getStatusEstagio());
        verify(estagioRepository, times(1)).save(estagio);
        verify(notificacaoService, times(1)).criarNotificacao(any(Usuario.class), anyString(), anyString(), any(TipoNotificacao.class));
    }

    @Test
    void getPendenciasDashboard_AsCoordenador_ReturnsAggregatedDTO() {
        Usuario coordenador = new Usuario();
        coordenador.setRole(Role.ROLE_COORDENADOR);

        when(estagioRepository.findByStatusEstagio(StatusEstagio.EM_ANALISE)).thenReturn(List.of(estagio));
        when(estagioRepository.findByStatusEstagio(StatusEstagio.ANALISE_CONCLUIDO)).thenReturn(List.of());
        when(estagioRepository.findByStatusEstagio(StatusEstagio.ANALISE_RESCINDIDO)).thenReturn(List.of());
        when(aditivoRepository.findByStatus(StatusAditivo.EM_ANALISE)).thenReturn(List.of());
        when(relatorioRepository.findByStatus(StatusRelatorio.EM_ANALISE)).thenReturn(List.of());

        DashboardPendenciasDTO dto = estagioService.getPendenciasDashboard(coordenador);

        assertNotNull(dto);
        assertEquals(1, dto.estagiosPendentes().size());
    }
}
