package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.AditivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService; //teste

    @PatchMapping("/{id}/aprovar-aditivo")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> aprovarAditivo(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado){
        aditivoService.aprovarAditivo(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> rejeitarAditivo(@PathVariable UUID id, @Valid @RequestBody RejeicaoDTO dto, @AuthenticationPrincipal Usuario usuarioLogado){
        aditivoService.rejeitarAditivo(id, dto, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
