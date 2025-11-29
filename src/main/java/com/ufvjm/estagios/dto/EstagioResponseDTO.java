package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.StatusEstagio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EstagioResponseDTO(UUID id, StatusEstagio statusEstagio, String alunoNomeCompleto, String orientadorNomeCompleto, String concedente, String supervisor, String formacaoSupervisor, LocalDate dataInicio, LocalDate dataTermino, Integer cargaHorariaSemanal, BigDecimal valorBolsa, Boolean auxilioTransporte, BigDecimal valorAuxilioTransporte, Boolean seguro, LocalDate dataEntregaTCE, LocalDate dataEntregaPlanoDeAtividades, List<RelatorioResponseDTO> relatorios, List<AditivoResponseDTO> aditivos) {
}
