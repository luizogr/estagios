package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.MensagemDTO;
import com.ufvjm.estagios.entities.*;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.TipoNotificacao;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.NotificacaoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

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
        logger.info("Iniciando envio de mensagem para estágio: {} do usuário: {}", estagioId, remetente.getId());

        // Validação de entrada
        if (estagioId == null || mensagemDTO == null || remetente == null) {
            logger.error("Parâmetros inválidos na requisição de envio de mensagem");
            throw new IllegalArgumentException("Parâmetros obrigatórios não fornecidos");
        }

        // 1. Buscar o estágio
        Estagio estagio = estagioRepository.findById(estagioId)
                .orElseThrow(() -> {
                    logger.warn("Estágio não encontrado: {}", estagioId);
                    return new IllegalArgumentException("Estágio não encontrado");
                });

        // 2. Buscar o aluno vinculado ao estágio
        Aluno aluno = estagio.getAluno();
        if (aluno == null) {
            logger.error("Aluno não associado ao estágio: {}", estagioId);
            throw new IllegalStateException("Estágio sem aluno associado");
        }

        Usuario usuarioAluno = aluno.getUsuario();
        if (usuarioAluno == null) {
            logger.error("Usuário não associado ao aluno do estágio: {}", estagioId);
            throw new IllegalStateException("Aluno sem usuário associado");
        }

        // 3. Validar permissões do remetente
        validarPermissaoEnvioMensagem(remetente, estagio);

        // 4. Enviar a notificação
        try {
            criarNotificacao(
                    usuarioAluno,
                    mensagemDTO.titulo(),
                    mensagemDTO.descricao(),
                    TipoNotificacao.MENSAGEM
            );
            logger.info("Mensagem enviada com sucesso para aluno {} do estágio {}", aluno.getId(), estagioId);
        } catch (Exception e) {
            logger.error("Erro ao criar notificação para o aluno {} do estágio {}: {}", 
                    aluno.getId(), estagioId, e.getMessage());
            throw new RuntimeException("Falha ao enviar mensagem", e);
        }
    }

    /**
     * Valida se o remetente tem permissão de enviar mensagens para o estágio
     */
    private void validarPermissaoEnvioMensagem(Usuario remetente, Estagio estagio) {
        Role role = remetente.getRole();

        // Coordenador pode enviar para qualquer estágio
        if (role == Role.ROLE_COORDENADOR) {
            logger.debug("Coordenador {} autorizado a enviar mensagem", remetente.getId());
            return;
        }

        // Professor pode enviar apenas se for orientador do estágio
        if (role == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(remetente)
                    .orElseThrow(() -> {
                        logger.warn("Perfil de professor não encontrado para usuário: {}", remetente.getId());
                        return new IllegalStateException("Perfil de professor não encontrado");
                    });

            if (estagio.getOrientador() != null && estagio.getOrientador().equals(professor)) {
                logger.debug("Professor {} autorizado como orientador do estágio {}", professor.getId(), estagio.getId());
                return;
            }

            logger.warn("Professor {} não é orientador do estágio {}", professor.getId(), estagio.getId());
            throw new AccessDeniedException("Você não é o orientador deste estágio");
        }

        // Aluno não pode enviar mensagens
        logger.warn("Usuário com role {} tentou enviar mensagem", role);
        throw new AccessDeniedException("Seu role não tem permissão para enviar mensagens");
    }
}