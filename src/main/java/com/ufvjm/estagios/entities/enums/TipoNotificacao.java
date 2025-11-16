package com.ufvjm.estagios.entities.enums;

public enum TipoNotificacao {
    // Pendências (Laranja)
    PENDENCIA,        // "Documento pendente"
    AVISO_PRAZO,      // "Relatório vence em 7 dias"

    // Alertas (Vermelho)
    ATRASADO,         // "Relatório atrasado"
    REJEITADO,        // "Estágio Rejeitado"

    // Sucesso (Verde)
    APROVADO,

    // Informativo (Azul/Roxo)
    MENSAGEM,         // "Mensagem do Professor"
    PRORROGACAO,      // "Notificação de prorrogação"
    DOCUMENTO         // "Aditivo registrado"
}
