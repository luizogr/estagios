package com.ufvjm.estagios.entities;

import com.ufvjm.estagios.entities.enums.StatusAditivo;
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

    private LocalDate novaDataTermino;

    @Enumerated(EnumType.STRING)
    private StatusAditivo status;

    public Aditivo() {
    }

    public Aditivo(UUID id, Estagio estagio, LocalDate dataAditivo, StatusAditivo status) {
        this.id = id;
        this.estagio = estagio;
        this.novaDataTermino = dataAditivo;
        this.status = status;
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

    public LocalDate getNovaDataTermino() {
        return novaDataTermino;
    }

    public void setNovaDataTermino(LocalDate novaDataTermino) {
        this.novaDataTermino = novaDataTermino;
    }

    public StatusAditivo getStatus() {
        return status;
    }

    public void setStatus(StatusAditivo status) {
        this.status = status;
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
