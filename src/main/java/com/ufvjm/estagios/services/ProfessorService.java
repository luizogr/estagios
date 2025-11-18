package com.ufvjm.estagios.services;

import com.ufvjm.estagios.dto.ProfessorRegisterDTO;
import com.ufvjm.estagios.dto.ProfessorSimpleDTO;
import com.ufvjm.estagios.dto.ResponseDTO;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<ProfessorSimpleDTO> getListaProfessores() {

        List<Professor> professores = professorRepository.findAll();

        return professores.stream()
                .map(professor -> new ProfessorSimpleDTO(
                        professor.getId(),
                        professor.getUsuario().getNome() // Pega o nome do usuário associado
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Professor registerProfessor(ProfessorRegisterDTO body){
        // 1. Verifica se o e-mail já existe
        if(this.usuarioRepository.findByEmailInstitucional(body.emailInstitucional()).isPresent()){
            throw new RuntimeException("E-mail já cadastrado.");
        }

        // 2. Cria a entidade Usuario
        Usuario newUsuario = new Usuario();
        newUsuario.setSenha(passwordEncoder.encode(body.senha()));
        newUsuario.setEmailInstitucional(body.emailInstitucional());
        newUsuario.setNome(body.nome());
        newUsuario.setRole(Role.ROLE_PROFESSOR);
        Usuario usuarioSalvo = this.usuarioRepository.save(newUsuario);

        // 3. Cria a entidade Professor e liga ao Usuario
        Professor newProfessor = new Professor();
        newProfessor.setSiap(body.siap());
        newProfessor.setUsuario(usuarioSalvo);
        //Professor professor = this.professorRepository.save(newProfessor);

        return this.professorRepository.save(newProfessor);
    }
}
