package com.ufvjm.estagios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.ufvjm.estagios.infra.validation.DominioEspecifico;
import jakarta.validation.constraints.Pattern;

public record AlunoRegisterDTO (String nome, @NotBlank @Email @DominioEspecifico(dominio = "ufvjm.edu.br") String emailInstitucional, String senha, @Pattern(regexp = "\\d{11}", message = "A matrícula deve conter exatamente 11 números.") String matricula){
}
