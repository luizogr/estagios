package com.ufvjm.estagios.services;

import com.ufvjm.estagios.repositories.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class EstagioServiceTest {

    @Mock
    private EstagioRepository estagioRepository;
    Mock
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

    @Test
    void criarEstagio() {
    }

    @Test
    void aprovarEstagio() {
    }

    @Test
    void listarTodosEstagios() {
    }

    @Test
    void getEstagioById() {
    }

    @Test
    void findEstagiosByAluno() {
    }

    @Test
    void findEstagiosByProfessor() {
    }

    @Test
    void proporAditivo() {
    }

    @Test
    void concluirEstagio() {
    }

    @Test
    void aprovarConclusao() {
    }

    @Test
    void rescindirEstagio() {
    }

    @Test
    void aprovarRescisao() {
    }

    @Test
    void atualizarEstagio() {
    }

    @Test
    void rejeitarEstagio() {
    }

    @Test
    void listarEstagiosDashboard() {
    }

    @Test
    void getPendenciasDashboard() {
    }
}