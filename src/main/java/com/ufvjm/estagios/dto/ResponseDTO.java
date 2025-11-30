package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.Role;

import java.util.UUID;

public record ResponseDTO (String name, String token, UUID profileId, Role role){
    public ResponseDTO(String name, String token, Role role) {
        this(name, token, null, role);
    }
}
