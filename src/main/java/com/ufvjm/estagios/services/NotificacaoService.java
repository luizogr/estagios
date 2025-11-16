package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Transactional
    public void criarNotificacao(Usuario destinatario, String titulo, String descricao, TipoNotificacao tipo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(destinatario);
        notificacao.setTitulo(titulo);
        notificacao.setDescricao(descricao);
        notificacao.setTipo(tipo);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);

    }
}
