package com.ufvjm.estagios.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RelatorioResponseDTO(UUID id, String titulo, LocalDate dataEntregaPrevista, LocalDate dataEntregaEfetiva, String statusTexto, String statusCor) {
}
