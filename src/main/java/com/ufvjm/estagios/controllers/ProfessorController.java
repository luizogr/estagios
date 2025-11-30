package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.DashboardPendenciasDTO;
import com.ufvjm.estagios.dto.EstagioResponseDTO;
import com.ufvjm.estagios.dto.ProfessorRegisterDTO;
import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.EstagioService;
import com.ufvjm.estagios.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    @Autowired
    private EstagioService estagioService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<List<ProfessorSimpleDTO>> getListaProfessores() {
        List<ProfessorSimpleDTO> lista = professorService.getListaProfessores();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/meus-estagios")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<Estagio>> getMeusEstagios(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Estagio> listaEstagios = estagioService.findEstagiosByProfessor(usuarioLogado);
        return ResponseEntity.ok(listaEstagios);
    }

    @PostMapping("/cadastrar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<Professor> cadastrarProfessor(@RequestBody ProfessorRegisterDTO dto) {
        Professor novoProfessor = professorService.registerProfessor(dto);

        // Retorna 201 Created e o objeto criado (sem token!)
        URI uri = URI.create("/api/professores/" + novoProfessor.getId());
        return ResponseEntity.created(uri).body(novoProfessor);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<List<EstagioResponseDTO>> listarEstagiosDashboard(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        List<EstagioResponseDTO> lista = estagioService.listarEstagiosDashboard(usuarioLogado);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/pendencias")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<DashboardPendenciasDTO> getPendencias(@AuthenticationPrincipal Usuario usuarioLogado) {
        DashboardPendenciasDTO pendencias = estagioService.getPendenciasDashboard(usuarioLogado);
        return ResponseEntity.ok(pendencias);
    }
}
