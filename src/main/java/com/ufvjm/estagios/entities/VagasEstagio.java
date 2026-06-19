package com.ufvjm.estagios.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_vagas_de_estagio")
@Getter
@Setter
@Data
public class VagasEstagio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String urlVaga;
    @Column(columnDefinition = "TEXT")
    private String urlPdfDrive;

    @Column(columnDefinition = "TEXT")
    private String descricao;
    private String titulo;

    private LocalDate dataDePublicacao;

    public VagasEstagio(){
    }

    public VagasEstagio(String descricao, UUID id, String urlVaga, String titulo) {
        this.descricao = descricao;
        this.id = id;
        this.urlVaga = urlVaga;
        this.titulo = titulo;
    }
}
