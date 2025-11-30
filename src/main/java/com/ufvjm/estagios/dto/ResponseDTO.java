package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.Role;

import java.util.UUID;

public record ResponseDTO (String name, String token, String role, UUID profileId){
    public ResponseDTO(String name, String token, String role) {
        this(name, token, role, null);
    }
}
