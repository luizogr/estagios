package com.ufvjm.estagios.dto;

import java.util.UUID;

public record ProfessorSimpleDTO (UUID id, String nome, String email, String siap, int totalEsagiosativos){
}
