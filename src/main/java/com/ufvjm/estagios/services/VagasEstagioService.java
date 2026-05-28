package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.VagasEstagioCreateDTO;
import com.ufvjm.estagios.dto.VagasEstagioDTO;
import com.ufvjm.estagios.dto.VagasEstagioUpdateDTO;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.VagasEstagio;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.VagasEstagioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VagasEstagioService {

    @Autowired
    private VagasEstagioRepository vagasEstagioRepository;
    @Autowired
    private ProfessorRepository professorRepository;


    public VagasEstagioDTO createVagaEstagio(VagasEstagioCreateDTO dto) {
        VagasEstagio vagasEstagio = new VagasEstagio();//gera ID automatico

        vagasEstagio.setTitulo(dto.titulo());
        vagasEstagio.setDescricao(dto.descricao());
        vagasEstagio.setUrlVaga(dto.urlVaga());
        vagasEstagio.setUrlPdfDrive(dto.urlPdfDrive());

        vagasEstagioRepository.save(vagasEstagio);

        VagasEstagioDTO vagasEstagioDTO = converterVagasParaDTO(vagasEstagio);

        return vagasEstagioDTO;
    }

    public List<VagasEstagioDTO> listarTodasVagas(){
        List<VagasEstagio> listaVagas = vagasEstagioRepository.findAll();
        List<VagasEstagioDTO> listaVagasDTO = new ArrayList<>();
        for(VagasEstagio v : listaVagas){
            listaVagasDTO.add(converterVagasParaDTO(v));
        }
        return listaVagasDTO;
    }

    public VagasEstagioDTO converterVagasParaDTO(VagasEstagio vagas){
        return new VagasEstagioDTO(vagas.getId(), vagas.getTitulo(), vagas.getDescricao(), vagas.getUrlVaga(), vagas.getUrlPdfDrive());
    }

    public VagasEstagioDTO editarVagEstagio(UUID id, VagasEstagioUpdateDTO dto, Usuario usuarioLogado){
        VagasEstagio vagasEstagio = vagasEstagioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga de Estágio não encontrada"));
            vagasEstagio.setTitulo(dto.titulo());
            vagasEstagio.setDescricao(dto.descricao());
            vagasEstagio.setUrlVaga(dto.urlVaga());
            vagasEstagio.setUrlPdfDrive(dto.urlPdfDrive());

        vagasEstagioRepository.save(vagasEstagio);
        VagasEstagioDTO vagaEstagioDTO = converterVagasParaDTO(vagasEstagio);

            return vagaEstagioDTO;
    }

    public void deletarVagEstagio(UUID id, Usuario usuarioLogado){
        VagasEstagio vagasEstagio = vagasEstagioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga de Estágio não encontrada"));
        vagasEstagioRepository.delete(vagasEstagio);
    }


}