package com.ufvjm.estagios.dto;

import java.util.UUID;

public record ResponseDTO (String name, String token, UUID profileId){//Conferir quais dados o front precisa
    public ResponseDTO(String name, String token) {
        this(name, token, null);
    }
}
