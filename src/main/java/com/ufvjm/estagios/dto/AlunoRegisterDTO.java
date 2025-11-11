package com.ufvjm.estagios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.ufvjm.estagios.infra.validation.DominioEspecifico;

public record AlunoRegisterDTO (String nome, @NotBlank @Email @DominioEspecifico(dominio = "ufvjm.edu.br") String emailInstitucional, String senha, String matricula){
}
