package com.ufvjm.estagios.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enviarEmailConfirmacaoSucesso() {

        String emailDestino = "estudante@ufvjm.edu.br";
        String token = "token-xyz-123";
        String linkEsperado = "https://sistemaestagios.squareweb.app/auth/confirmar?token=" + token;

        emailService.enviarEmailConfirmacao(emailDestino, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage emailEnviado = messageCaptor.getValue();

        assertEquals("noreply@seusistema.com", emailEnviado.getFrom());
        assertEquals(emailDestino, emailEnviado.getTo()[0]);
        assertEquals("Confirme seu cadastro no Sistema de Estágios", emailEnviado.getSubject());

        assertTrue(emailEnviado.getText().contains(linkEsperado));
        assertTrue(emailEnviado.getText().contains("Olá! Clique no link para ativar sua conta:"));
    }

    @Test
    void enviarEmailConfirmacaoErro() {
        String emailDestino = "estudante@abc.com.br";
        String token = "token-xyz-123";
        org.mockito.Mockito.doThrow(new RuntimeException("Servidor SMTP fora do ar"))
                .when(mailSender).send(org.mockito.Mockito.any(SimpleMailMessage.class));

        emailService.enviarEmailConfirmacao(emailDestino, token);

        verify(mailSender).send(org.mockito.Mockito.any(SimpleMailMessage.class));
    }
}