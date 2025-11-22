package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.*;
import com.ufvjm.estagios.entities.Aditivo;
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
    public ResponseEntity<EstagioResponseDTO> getEstagioById(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        EstagioResponseDTO estagio = estagioService.getEstagioById(id, usuarioLogado);
        return ResponseEntity.ok(estagio);
    }

    @PostMapping("/{id}/aditivos")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Aditivo> proporAditivo(@PathVariable UUID id, @RequestBody AditivoCreateDTO dto){
        Aditivo novoAditivo = estagioService.proporAditivo(id, dto);
        return ResponseEntity.ok(novoAditivo);
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Estagio> concluirEstagio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado){
        Estagio estagio = estagioService.concluirEstagio(id, usuarioLogado);
        return ResponseEntity.ok(estagio);
    }

    @PatchMapping("/{id}/aprovar-conclusao")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<Void> aprovarConclusao(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        estagioService.aprovarConclusao(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/rescindir")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Estagio> rescindirEstagio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado){
        Estagio estagio = estagioService.rescindirEstagio(id, usuarioLogado);
        return ResponseEntity.ok(estagio);
    }

    @PatchMapping("/{id}/aprovar-rescisao")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<Void> aprovarRescisao(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        estagioService.aprovarRescisao(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/atualizar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ALUNO')")
    public ResponseEntity<Estagio> atualizarEstagio(@PathVariable UUID id, @Valid @RequestBody EstagioUpdateDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        Estagio estagioAtualizado = estagioService.atualizarEstagio(id,dto, usuarioLogado);
        return ResponseEntity.ok(estagioAtualizado);
    }

    @PatchMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public ResponseEntity<Void> rejeitarEstagio(@PathVariable UUID id, @Valid @RequestBody RejeicaoDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        estagioService.rejeitarEstagio(id, dto, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
