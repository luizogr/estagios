package com.ufvjm.estagios.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VagasEstagioDTO(UUID id, String titulo, String descricao, String urlVaga, String urlPdfDrive, LocalDate dataDePublicacao) {
}
