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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@seusistema.com"); // Alguns servidores exigem remetente
            message.setTo(emailDestino);
            message.setSubject("Confirme seu cadastro no Sistema de Estágios");

            String link = "https://sistemaestagios.squareweb.app/auth/confirmar?token=" + token;

            message.setText("Olá! Clique no link para ativar sua conta: " + link);

            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso para: " + emailDestino);

        } catch (Exception e) {
            // ISSO VAI MOSTRAR O ERRO REAL NO CONSOLE
            System.err.println("FALHA AO ENVIAR E-MAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
