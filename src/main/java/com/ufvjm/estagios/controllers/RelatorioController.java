package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.RelatorioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    @Autowired
    private RelatorioService relatorioService;

    @PatchMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> rejeitarRelatorio(@PathVariable UUID id, @Valid @RequestBody RejeicaoDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        relatorioService.rejeitarRelatorio(id, dto, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> aprovarRelatorio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        relatorioService.aprovarRelatorio(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> entregarRelatorio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado) {
        relatorioService.entregarRelatorio(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
