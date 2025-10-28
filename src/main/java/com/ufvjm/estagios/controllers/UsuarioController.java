package com.ufvjm.estagios.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UsuarioController {

    @GetMapping("/test")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("sucesso");
    }
}
