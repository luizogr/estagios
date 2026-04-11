package com.ufvjm.estagios.dto;

import java.util.UUID;

public record VagasEstagioDTO(UUID id, String titulo, String descricao, String urlVaga, String urlPdfDrive) {
}
