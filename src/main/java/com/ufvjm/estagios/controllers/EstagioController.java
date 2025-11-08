package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.EstagioCreateDTO;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.EstagioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estagios")
public class EstagioController {

    @Autowired
    private EstagioService estagioService;

    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR', 'ALUNO')")
    @PostMapping("/criar")
    public ResponseEntity<Estagio> criarEstagio(@Valid @RequestBody EstagioCreateDTO dto){
        Estagio novoEstagio = estagioService.criarEstagio(dto);

        URI uri = URI.create("/api/estagios/" + novoEstagio.getId());
        return ResponseEntity.created(uri).body(novoEstagio);
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<Void> aprovarEstagio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        estagioService.aprovarEstagio(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<List<Estagio>> listarTodosEstagios(){
        List<Estagio> listaEstagios = estagioService.listarTodosEstagios();
        return ResponseEntity.ok(listaEstagios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<Estagio> getEstagioById(@PathVariable UUID id,  @AuthenticationPrincipal Usuario usuarioLogado) {
        Estagio estagio = estagioService.getEstagioById(id, usuarioLogado);
        return ResponseEntity.ok(estagio);
    }



}
