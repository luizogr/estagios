package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.StatusAditivo;

import java.time.LocalDate;
import java.util.UUID;

public record AditivoResponseDTO (UUID id, LocalDate novaDataTermino, StatusAditivo status, String descricao){
}
