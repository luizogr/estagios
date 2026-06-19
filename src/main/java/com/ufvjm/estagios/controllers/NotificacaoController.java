package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.MensagemDTO;
import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.services.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/minhas-notificacoes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacao>> getMinhasNotificacoes(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Notificacao> notificacoes = notificacaoRepository
                .findByDestinatarioOrderByDataCriacaoDesc(usuarioLogado);

        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping("/enviar-mensagem/{estagioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> enviarMensagem(
            @PathVariable UUID estagioId,
            @Valid @RequestBody MensagemDTO mensagemDTO,
            @AuthenticationPrincipal Usuario remetente
    ) {
        notificacaoService.enviarMensagemParaAluno(estagioId, mensagemDTO, remetente);
        return ResponseEntity.noContent().build();
    }
}