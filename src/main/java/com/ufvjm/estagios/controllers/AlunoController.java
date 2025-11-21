package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.EstagioResponseDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.EstagioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private EstagioService estagioService;

    @GetMapping("/meus-estagios")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<EstagioResponseDTO>> getMeusEstagios(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<EstagioResponseDTO> listaEstagios = estagioService.findEstagiosByAluno(usuarioLogado);
        return ResponseEntity.ok(listaEstagios);
    }

    /*@GetMapping
    public ResponseEntity<Aluno> findAll(){
        Aluno a = new Aluno(1L, "Luiz", "123456", "@ufvjm.edu.br", "1234");
        return ResponseEntity.ok().body(a);
    }*/

}
