package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.*;
import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.infra.security.TokenService;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final UsuarioRepository repository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final TokenService tokenService;
    @Autowired
    private final AlunoRepository alunoRepository;
    @Autowired
    private final ProfessorRepository professorRepository;

    public AuthController(UsuarioRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService, AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        Usuario usuario = this.repository.findByEmailInstitucional(body.emailInstitucional()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), usuario.getSenha())) {
            String token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token));//Ver quais informações o front vai precisar
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register/aluno")
    public ResponseEntity registerAluno(@RequestBody AlunoRegisterDTO body){
        // 1. Verifica se o e-mail já existe
        if(this.repository.findByEmailInstitucional(body.emailInstitucional()).isPresent()){
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        // 2. Cria a entidade Usuario
        Usuario newUsuario = new Usuario();
        newUsuario.setSenha(passwordEncoder.encode(body.senha()));
        newUsuario.setEmailInstitucional(body.emailInstitucional());
        newUsuario.setNome(body.nome());
        newUsuario.setRole(Role.ROLE_ALUNO);
        Usuario usuarioSalvo = this.repository.save(newUsuario); // Salva e recupera com o ID

        // 3. Cria a entidade Aluno e liga ao Usuario
        Aluno newAluno = new Aluno();
        newAluno.setMatricula(body.matricula());
        newAluno.setUsuario(usuarioSalvo);
        Aluno aluno = this.alunoRepository.save(newAluno);

        // 4. Gera o token e retorna
        String token = this.tokenService.generateToken(usuarioSalvo);
        return ResponseEntity.ok(new ResponseDTO(usuarioSalvo.getNome(), token, aluno.getId()));
    }

    @PostMapping("/register/professor")
    public ResponseEntity registerProfessor(@RequestBody ProfessorRegisterDTO body){
        // 1. Verifica se o e-mail já existe
        if(this.repository.findByEmailInstitucional(body.emailInstitucional()).isPresent()){
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        // 2. Cria a entidade Usuario
        Usuario newUsuario = new Usuario();
        newUsuario.setSenha(passwordEncoder.encode(body.senha()));
        newUsuario.setEmailInstitucional(body.emailInstitucional());
        newUsuario.setNome(body.nome());
        newUsuario.setRole(Role.ROLE_PROFESSOR);
        Usuario usuarioSalvo = this.repository.save(newUsuario);

        // 3. Cria a entidade Professor e liga ao Usuario
        Professor newProfessor = new Professor();
        newProfessor.setSiap(body.siap());
        newProfessor.setUsuario(usuarioSalvo);
        Professor professor = this.professorRepository.save(newProfessor);

        // 4. Gera o token e retorna
        String token = this.tokenService.generateToken(usuarioSalvo);
        return ResponseEntity.ok(new ResponseDTO(usuarioSalvo.getNome(), token, professor.getId()));
    }
}
