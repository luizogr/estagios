package com.ufvjm.estagios.dto;

import com.ufvjm.estagios.entities.enums.Role;

import java.util.UUID;

public record ResponseDTO (String name, String token, UUID profileId, Role role){//Conferir quais dados o front precisa
    public ResponseDTO(String name, String token) {
        this(name, token, null, null);
    }
}
