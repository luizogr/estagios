package com.ufvjm.estagios.repositories;

import com.ufvjm.estagios.entities.Notificacao;
import com.ufvjm.estagios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    List<Notificacao> findByDestinatarioOrderByDataCriacaoDesc(Usuario destinatario);
}
