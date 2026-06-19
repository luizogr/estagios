package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.MensagemDTO;
import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.services.NotificacaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoController.class);

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/minhas-notificacoes")
    @PreAuthorize("hasAnyRole('ALUNO','PROFESSOR','COORDENADOR')")
    public ResponseEntity<List<Notificacao>> getMinhasNotificacoes(@AuthenticationPrincipal Usuario usuarioLogado) {
        logger.info("Buscando notificações para usuário: {}", usuarioLogado.getId());
        List<Notificacao> notificacoes = notificacaoRepository
                .findByDestinatarioOrderByDataCriacaoDesc(usuarioLogado);

        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping("/{estagioId}/enviar-mensagem")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> enviarMensagem(
            @PathVariable("estagioId") UUID estagioId,
            @Valid @RequestBody MensagemDTO mensagemDTO,
            @AuthenticationPrincipal Usuario remetente
    ) {
        logger.info("Enviando mensagem para estágio: {}", estagioId);
        notificacaoService.enviarMensagemParaAluno(estagioId, mensagemDTO, remetente);
        return ResponseEntity.noContent().build();
    }
}