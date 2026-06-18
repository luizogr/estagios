package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.VagasEstagioCreateDTO;
import com.ufvjm.estagios.dto.VagasEstagioDTO;
import com.ufvjm.estagios.dto.VagasEstagioUpdateDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.VagasEstagio;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.repositories.VagasEstagioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VagasEstagioServiceTest {

    @Mock
    private VagasEstagioRepository vagasEstagioRepository;

    @InjectMocks
    private VagasEstagioService vagasEstagioService;

    private VagasEstagio vaga;
    private UUID vagaId;
    private Usuario usuarioLogado;

    @BeforeEach
    void setUp() {
        vagaId = UUID.fromString("00000000-0000-0000-0000-000000000321");
        vaga = new VagasEstagio( "Descrição da vaga", vagaId, "http://vaga.com", "http://pdf.com");
        usuarioLogado = new Usuario(UUID.fromString("00000000-0000-0000-0000-000000000123"), "Admin", "admin@ufvjm.edu.br", "senha", Role.ROLE_COORDENADOR);
    }

    @Test
    void createVagaEstagio_Success() {
        // Arrange
        VagasEstagioCreateDTO createDTO = new VagasEstagioCreateDTO("Nova Vaga", "Nova Descrição", "http://nova.com", "http://novopdf.com");
        ArgumentCaptor<VagasEstagio> vagaCaptor = ArgumentCaptor.forClass(VagasEstagio.class);

        // Act
        VagasEstagioDTO result = vagasEstagioService.createVagaEstagio(createDTO);

        // Assert
        verify(vagasEstagioRepository).save(vagaCaptor.capture());
        VagasEstagio savedVaga = vagaCaptor.getValue();

        assertNotNull(result);
        assertEquals(createDTO.titulo(), savedVaga.getTitulo());
        assertEquals(createDTO.descricao(), savedVaga.getDescricao());
        assertEquals(createDTO.urlVaga(), savedVaga.getUrlVaga());
        assertEquals(createDTO.urlPdfDrive(), savedVaga.getUrlPdfDrive());
    }

    @Test
    void listarTodasVagas_Success() {
        // Arrange
        when(vagasEstagioRepository.findAll()).thenReturn(Collections.singletonList(vaga));

        // Act
        List<VagasEstagioDTO> result = vagasEstagioService.listarTodasVagas();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        VagasEstagioDTO dto = result.get(0);
        assertEquals(vaga.getId(), dto.id());
        assertEquals(vaga.getTitulo(), dto.titulo());
    }

    @Test
    void editarVagEstagio_Success() {
        // Arrange
        VagasEstagioUpdateDTO updateDTO = new VagasEstagioUpdateDTO(vagaId, "Título Editado", "Descrição Editada", "http://editado.com", "http://editadopdf.com");
        when(vagasEstagioRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act
        VagasEstagioDTO result = vagasEstagioService.editarVagEstagio(updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updateDTO.id(), result.id());
        assertEquals(updateDTO.titulo(), result.titulo());
        assertEquals(updateDTO.descricao(), result.descricao());
        verify(vagasEstagioRepository).save(any(VagasEstagio.class));
    }

    @Test
    void editarVagEstagio_NotFound_ThrowsException() {
        // Arrange
        VagasEstagioUpdateDTO updateDTO = new VagasEstagioUpdateDTO(UUID.randomUUID(), "Título", "Descrição", "", "");
        when(vagasEstagioRepository.findById(updateDTO.id())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vagasEstagioService.editarVagEstagio(updateDTO);
        });
        assertEquals("Vaga de Estágio não encontrada", exception.getMessage());
    }

    @Test
    void deletarVagEstagio_Success() {
        // Arrange
        when(vagasEstagioRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act
        vagasEstagioService.deletarVagEstagio(vagaId, usuarioLogado);

        // Assert
        verify(vagasEstagioRepository, times(1)).delete(vaga);
    }

    @Test
    void deletarVagEstagio_NotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(vagasEstagioRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vagasEstagioService.deletarVagEstagio(nonExistentId, usuarioLogado);
        });
        assertEquals("Vaga de Estágio não encontrada", exception.getMessage());
    }
}