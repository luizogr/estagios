package com.ufvjm.estagios.controllers;

import com.ufvjm.estagios.dto.*;
import com.ufvjm.estagios.entities.Aluno;
import com.ufvjm.estagios.entities.Professor;
import com.ufvjm.estagios.entities.TokenVerificacao;
import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.entities.enums.Role;
import com.ufvjm.estagios.infra.security.TokenService;
import com.ufvjm.estagios.repositories.AlunoRepository;
import com.ufvjm.estagios.repositories.ProfessorRepository;
import com.ufvjm.estagios.repositories.TokenVerificacaoRepository;
import com.ufvjm.estagios.repositories.UsuarioRepository;
import com.ufvjm.estagios.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @Autowired
    private TokenVerificacaoRepository tokenVerificacaoRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

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

        if (!usuario.isAtivo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não ativado. Verifique seu e-mail.");
        }

        if(passwordEncoder.matches(body.password(), usuario.getSenha())) {
            String token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token, usuario.getRole().name()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register/aluno")
    public ResponseEntity registerAluno(@RequestBody AlunoRegisterDTO body){
        // 1. Verifica se o e-mail já existe
        if(this.repository.findByEmailInstitucional(body.emailInstitucional()).isPresent()){
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        if (alunoRepository.findByMatricula(body.matricula()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Matrícula já cadastrada.");
        }

        // 2. Cria a entidade Usuario
        Usuario newUsuario = new Usuario();
        newUsuario.setSenha(passwordEncoder.encode(body.senha()));
        newUsuario.setEmailInstitucional(body.emailInstitucional());
        newUsuario.setNome(body.nome());
        newUsuario.setRole(Role.ROLE_ALUNO);
        newUsuario.setAtivo(false);
        Usuario usuarioSalvo = this.repository.save(newUsuario); // Salva e recupera com o ID

        // 3. Cria a entidade Aluno e liga ao Usuario
        Aluno newAluno = new Aluno();
        newAluno.setMatricula(body.matricula());
        newAluno.setUsuario(usuarioSalvo);
        Aluno aluno = this.alunoRepository.save(newAluno);

        TokenVerificacao tokenVerificacao = new TokenVerificacao(usuarioSalvo);
        tokenVerificacaoRepository.save(tokenVerificacao);

        // 4. Gera o token e retorna
        emailService.enviarEmailConfirmacao(usuarioSalvo.getEmailInstitucional(), tokenVerificacao.getToken());

        return ResponseEntity.ok("Cadastro realizado! Verifique seu e-mail para ativar.");
    }

    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmarEmail(@RequestParam("token") String token) {
        // 1. Busca o token no banco
        TokenVerificacao tokenVerificacao = tokenVerificacaoRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // 2. Verifica se expirou
        if (tokenVerificacao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expirado.");
        }

        // 3. Ativa o usuário
        Usuario usuario = tokenVerificacao.getUsuario();
        usuario.setAtivo(true);
        repository.save(usuario);

        return ResponseEntity.ok("Conta ativada com sucesso! Agora você pode fazer login.");
    }
}
