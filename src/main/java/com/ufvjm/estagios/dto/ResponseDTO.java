package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.Role;

import java.util.UUID;

public record ResponseDTO (UUID id, String name, String token, String role, UUID alunoId){
    /*public ResponseDTO(UUID id, String name, String token, String role, UUID alunoId) {
        this(id, name, token, role, alunoId);
    }*/
}
