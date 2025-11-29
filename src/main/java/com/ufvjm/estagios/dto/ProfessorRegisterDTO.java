package com.ufvjm.estagios.dto;

import jakarta.validation.constraints.Pattern;

public record ProfessorRegisterDTO (String nome, String emailInstitucional, String senha, @Pattern(regexp = "\\d{7}", message = "O SIAP deve conter exatamente 7 números.") String siap){
}
