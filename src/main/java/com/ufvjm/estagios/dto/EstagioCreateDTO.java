package com.ufvjm.estagios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EstagioCreateDTO (UUID alunoId, UUID orientadorId, String concedente, String supervisor, LocalDate dataInicio, LocalDate dataTermino, Integer cargaHoraria, BigDecimal valorBolsa, Boolean auxilioTransporte, BigDecimal valorAuxilioTransporte, Boolean seguro, LocalDate dataEntregaTCE, LocalDate dataEntregaPlanoAtividade){
}
