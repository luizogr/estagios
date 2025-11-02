package com.ufvjm.estagios.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_aditivos")
public class Aditivo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_estagio")
    private Estagio estagio;

    private LocalDate dataAditivo;

    public Aditivo() {
    }

    public Aditivo(UUID id, Estagio estagio, LocalDate dataAditivo) {
        this.id = id;
        this.estagio = estagio;
        this.dataAditivo = dataAditivo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Estagio getEstagio() {
        return estagio;
    }

    public void setEstagio(Estagio estagio) {
        this.estagio = estagio;
    }

    public LocalDate getDataAditivo() {
        return dataAditivo;
    }

    public void setDataAditivo(LocalDate dataAditivo) {
        this.dataAditivo = dataAditivo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Aditivo aditivo = (Aditivo) o;
        return Objects.equals(id, aditivo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
