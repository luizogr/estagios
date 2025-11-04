package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<ProfessorSimpleDTO> getListaProfessores() {

        List<Professor> professores = professorRepository.findAll();

        return professores.stream()
                .map(professor -> new ProfessorSimpleDTO(
                        professor.getId(),
                        professor.getUsuario().getNome() // Pega o nome do usuário associado
                ))
                .collect(Collectors.toList());
    }
}
