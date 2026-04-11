package com.ufvjm.estagios.controllers;


import com.ufvjm.estagios.dto.VagasEstagioCreateDTO;
import com.ufvjm.estagios.entities.VagasEstagio;
import com.ufvjm.estagios.services.VagasEstagioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vagas-estagio")
public class VagasEstagioController {
    @Autowired
    private VagasEstagioService vagasEstagioService;


    @PostMapping("/criar")
    //@PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    public VagasEstagio criarVagaEstagio(@RequestBody VagasEstagioCreateDTO dto) {
        // Lógica para criar uma vaga de estágio
        return vagasEstagioService.createVagaEstagio(dto);
    }


}
