package com.ufvjm.estagios.dto;

import java.util.List;

public record DashboardPendenciasDTO(
        List<EstagioResponseDTO> estagiosPendentes,
        List<AditivoResponseDTO> aditivosPendentes,
        List<RelatorioResponseDTO> relatoriosPendentes,
        List<EstagioResponseDTO> conclusoesPendentes,
        List<EstagioResponseDTO> rescisoesPendentes
) {}