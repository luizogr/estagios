package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.EstagioService;
import com.ufvjm.estagios.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
