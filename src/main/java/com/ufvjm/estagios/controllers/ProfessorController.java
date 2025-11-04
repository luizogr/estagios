package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<List<ProfessorSimpleDTO>> getListaProfessores() {
        List<ProfessorSimpleDTO> lista = professorService.getListaProfessores();
        return ResponseEntity.ok(lista);
    }
}
