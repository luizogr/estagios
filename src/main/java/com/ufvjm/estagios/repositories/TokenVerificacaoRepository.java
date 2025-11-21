package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.TokenVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenVerificacaoRepository extends JpaRepository<TokenVerificacao, UUID> {
    public Optional<TokenVerificacao> findByToken(String token);
}
