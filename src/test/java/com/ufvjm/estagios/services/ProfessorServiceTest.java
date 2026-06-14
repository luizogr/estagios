package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.ProfessorRegisterDTO;
import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EstagioRepository estagioRepository;

    @InjectMocks
    private ProfessorService professorService;

    private Usuario usuario;
    private Professor professor;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(UUID.fromString("00000000-0000-0000-0000-000000000123"));
        usuario.setNome("Professor Teste");
        usuario.setEmailInstitucional("teste@ufvjm.edu.br");

        professor = new Professor();
        professor.setId(UUID.fromString("00000000-0000-0000-0000-000000000321"));
        professor.setSiap("123456");
        professor.setUsuario(usuario);
    }

    @Test
    void getListaProfessores_Success() {
        // Arrange
        Estagio estagioAtivo = new Estagio();
        estagioAtivo.setStatusEstagio(StatusEstagio.ATIVO);

        Estagio estagioInativo = new Estagio();
        estagioInativo.setStatusEstagio(StatusEstagio.CONCLUIDO);

        when(professorRepository.findAll()).thenReturn(Collections.singletonList(professor));
        when(estagioRepository.findByOrientador(professor)).thenReturn(List.of(estagioAtivo, estagioInativo));

        // Act
        List<ProfessorSimpleDTO> result = professorService.getListaProfessores();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ProfessorSimpleDTO dto = result.get(0);
        assertEquals(professor.getId(), dto.id());
        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getEmailInstitucional(), dto.email());
        assertEquals(professor.getSiap(), dto.siap());
        assertEquals(1, dto.totalEsagiosativos()); // Apenas 1 estágio ativo
    }

    @Test
    void registerProfessor_Success() {
        // Arrange
        ProfessorRegisterDTO registerDTO = new ProfessorRegisterDTO("Novo Professor", "novo@ufvjm.edu.br", "senha123", "654321");
        String encodedPassword = "encodedPassword";

        when(usuarioRepository.findByEmailInstitucional(registerDTO.emailInstitucional())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.senha())).thenReturn(encodedPassword);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Professor result = professorService.registerProfessor(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(registerDTO.siap(), result.getSiap());
        assertNotNull(result.getUsuario());
        assertEquals(registerDTO.nome(), result.getUsuario().getNome());
        assertEquals(registerDTO.emailInstitucional(), result.getUsuario().getEmailInstitucional());
        assertEquals(encodedPassword, result.getUsuario().getSenha());
        assertEquals(Role.ROLE_PROFESSOR, result.getUsuario().getRole());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    void registerProfessor_EmailAlreadyExists_ThrowsException() {
        // Arrange
        ProfessorRegisterDTO registerDTO = new ProfessorRegisterDTO("Professor Existente", "teste@ufvjm.edu.br", "senha123", "987654");

        when(usuarioRepository.findByEmailInstitucional(registerDTO.emailInstitucional())).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            professorService.registerProfessor(registerDTO);
        });

        assertEquals("E-mail já cadastrado.", exception.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(professorRepository, never()).save(any(Professor.class));
    }
}