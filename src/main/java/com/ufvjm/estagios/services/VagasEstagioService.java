package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.VagasEstagioCreateDTO;
import com.ufvjm.estagios.dto.VagasEstagioDTO;
import com.ufvjm.estagios.entities.VagasEstagio;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.VagasEstagioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VagasEstagioService {

    @Autowired
    private VagasEstagioRepository vagasEstagioRepository;
    @Autowired
    private ProfessorRepository professorRepository;


    public VagasEstagio createVagaEstagio(VagasEstagioCreateDTO dto) {
        VagasEstagio vagasEstagio = new VagasEstagio();//gera ID automatico

        vagasEstagio.setTitulo(dto.titulo());
        vagasEstagio.setDescricao(dto.descricao());
        vagasEstagio.setUrlVaga(dto.urlVaga());
        vagasEstagio.setUrlPdfDrive(dto.urlPdfDrive());

        return vagasEstagioRepository.save(vagasEstagio);
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
}
