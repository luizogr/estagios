package com.ufvjm.estagios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EstagioUpdateDTO(UUID orientadorId, String concedente, String supervisor, String formacaoSupervisor, LocalDate dataInicio, LocalDate dataTermino, Integer cargaHoraria, BigDecimal valorBolsa, Boolean auxilioTransporte, BigDecimal valorAuxilioTransporte, Boolean seguro, LocalDate dataEntregaTCE, LocalDate dataEntregaPlanoAtividade) {
}
