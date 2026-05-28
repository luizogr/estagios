package com.ufvjm.estagios.controllers;


import com.ufvjm.estagios.dto.VagasEstagioCreateDTO;
import com.ufvjm.estagios.dto.VagasEstagioDTO;
import com.ufvjm.estagios.dto.VagasEstagioUpdateDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.VagasEstagio;
import com.ufvjm.estagios.services.VagasEstagioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vagas-estagio")
public class VagasEstagioController {
    @Autowired
    private VagasEstagioService vagasEstagioService;

    @PostMapping("/criar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public VagasEstagioDTO criarVagaEstagio(@RequestBody VagasEstagioCreateDTO dto) {
        // Lógica para criar uma vaga de estágio
        return vagasEstagioService.createVagaEstagio(dto);
    }

    @GetMapping("/listar-vagas")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<VagasEstagioDTO>> listarTodasVagas(){
        List<VagasEstagioDTO> listaVagas = vagasEstagioService.listarTodasVagas();
        return ResponseEntity.ok(listaVagas);
    }

    @PatchMapping("/{id}/editar-vaga")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public VagasEstagioDTO editarVagaEstagio(@RequestBody VagasEstagioUpdateDTO dto, @PathVariable UUID id){
        return vagasEstagioService.editarVagEstagio(id, dto, null);

    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<Void> deleteVagaEstagio(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado){
        vagasEstagioService.deletarVagEstagio(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
