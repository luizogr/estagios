package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.EstagioCreateDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.services.EstagioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class EstagioController {

    @Autowired
    private EstagioService estagioService;

    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR', 'ALUNO')")
    @PostMapping("/estagios")
    public ResponseEntity<Estagio> criarEstagio(@Valid @RequestBody EstagioCreateDTO dto){
        Estagio novoEstagio = estagioService.criarEstagio(dto);

        URI uri = URI.create("/api/estagios/" + novoEstagio.getId());
        return ResponseEntity.created(uri).body(novoEstagio);
    }

}
