package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.MensagemDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private EstagioRepository estagioRepository;

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

    @Transactional
    public void enviarMensagemParaAluno(UUID estagioId, MensagemDTO mensagemDTO, Usuario remetente) {
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> new RuntimeException("Estágio não encontrado"));

        Aluno aluno = estagio.getAluno();

        boolean temPermissao = false;
        if (remetente.getRole() == Role.ROLE_COORDENADOR) {
            temPermissao = true;
        } else if (remetente.getRole() == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(remetente)
                    .orElseThrow(() -> new RuntimeException("Perfil de professor não encontrado"));

            if(estagio.getOrientador().equals(professor)){
                temPermissao = true;
            }
        }

        if (!temPermissao) {
            throw new AccessDeniedException("Você não tem permissão para enviar mensagens para este aluno.");
        }

        criarNotificacao(aluno.getUsuario(), mensagemDTO.titulo(), mensagemDTO.descricao(), TipoNotificacao.MENSAGEM);
    }
}