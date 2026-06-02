package com.ufvjm.estagios.controllers.docs;

import com.ufvjm.estagios.dto.RejeicaoDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.services.AditivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aditivos")
public interface AditivoControllerDocs {



    @PatchMapping("/{id}/aprovar-aditivo")
    @Operation(summary = "Aprovar um aditivo de estágio",
            description = "Permite que um coordenador ou professor aprove um aditivo proposto por um aluno. O aditivo só pode ser aprovado se estiver em estado 'PENDENTE'."
            , tags = {"Aditivo"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Aditivo aprovado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não autenticado para esse estágio ou aditivo não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")}
    )
    ResponseEntity<Void> aprovarAditivo(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuarioLogado);

    @PatchMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'PROFESSOR')")
    @Operation(summary = "Aprovar um aditivo de estágio",
            description = "Permite que um coordenador ou professor aprove um aditivo proposto por um aluno. O aditivo só pode ser aprovado se estiver em estado 'PENDENTE'."
            , tags = {"Aditivo"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Aditivo reprovado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não autenticado para esse estágio ou aditivo não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")}
    )
    public ResponseEntity<Void> rejeitarAditivo(@PathVariable UUID id, @Valid @RequestBody RejeicaoDTO dto, @AuthenticationPrincipal Usuario usuarioLogado);
}
