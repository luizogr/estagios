package com.ufvjm.estagios.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailConfirmacao(String emailDestino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject("Confirme seu cadastro no Sistema de Estágios");

        // O link que o aluno vai clicar (no seu frontend ou backend direto)
        String link = "http://localhost:8080/auth/confirmar?token=" + token;

        message.setText("Olá! Clique no link para ativar sua conta: " + link);

        mailSender.send(message);
    }
}
