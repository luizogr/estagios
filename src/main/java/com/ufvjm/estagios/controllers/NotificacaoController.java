package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @GetMapping("/minhas-notificacoes")
    @PreAuthorize("isAuthenticated()") // Qualquer um logado pode ver
    public ResponseEntity<List<Notificacao>> getMinhasNotificacoes(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Notificacao> notificacoes = notificacaoRepository
                .findByDestinatarioOrderByDataCriacaoDesc(usuarioLogado);

        return ResponseEntity.ok(notificacoes);
    }
}
