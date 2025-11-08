package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Aditivo;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import com.ufvjm.estagios.repositories.AditivoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Service
public class AditivoService {

    @Autowired
    private AditivoRepository aditivoRepository;
    @Autowired
    private EstagioRepository estagioRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private RelatorioService relatorioService;

    @Transactional
    public void aprovarAditivo(@PathVariable UUID aditivoId, @AuthenticationPrincipal Usuario usuarioLogado){
        Aditivo aditivo = aditivoRepository.findById(aditivoId)
                .orElseThrow(() -> new RuntimeException("Aditivo nao encontrado"));

        boolean temPermissao = false;


        if (usuarioLogado.getRole() == Role.ROLE_COORDENADOR){
            temPermissao = true;
        } else if (usuarioLogado.getRole() == Role.ROLE_PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Perfil nao encontrado"));

            if (aditivo.getEstagio().getOrientador().equals(professor)){
                temPermissao = true;
            }
        }

        if (!temPermissao){
            throw new AccessDeniedException("Usuario nao permitido");
        }

        if (aditivo.getStatus() == StatusAditivo.EM_ANALISE){
            aditivo.setStatus(StatusAditivo.APROVADO);

            aditivoRepository.save(aditivo);
        } else {
            throw new RuntimeException("Aditivo não está em analise");
        }
    }
}
