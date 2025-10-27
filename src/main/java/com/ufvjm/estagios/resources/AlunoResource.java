package com.ufvjm.estagios.resources;

import com.ufvjm.estagios.entities.Aluno;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "alunos")
public class AlunoResource {

    /*@GetMapping
    public ResponseEntity<Aluno> findAll(){
        Aluno a = new Aluno(1L, "Luiz", "123456", "@ufvjm.edu.br", "1234");
        return ResponseEntity.ok().body(a);
    }*/

}
