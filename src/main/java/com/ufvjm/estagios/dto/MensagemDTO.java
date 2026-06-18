package com.ufvjm.estagios.dto;

import jakarta.validation.constraints.NotBlank;

public record MensagemDTO(
        @NotBlank
        String titulo,
        @NotBlank
        String descricao
) {
}