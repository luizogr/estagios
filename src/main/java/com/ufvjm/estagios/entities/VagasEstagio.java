package com.ufvjm.estagios.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private String urlVaga;
    private String urlPdfDrive;
    private String descricao;
    private String titulo;

    public VagasEstagio(){
    }

    public VagasEstagio(String descricao, UUID id, String urlurlVaga, String titulo) {
        this.descricao = descricao;
        this.id = id;
        this.urlVaga = urlVaga;
        this.titulo = titulo;
    }
}
