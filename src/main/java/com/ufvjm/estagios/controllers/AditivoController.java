package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.AditivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/aditivos")
public class AditivoController {

    @Autowired
    private AditivoService aditivoService;

    @PatchMapping("/{id}/aprovar-aditivo")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> aprovarAditivo(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado){
        aditivoService.aprovarAditivo(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }
}
