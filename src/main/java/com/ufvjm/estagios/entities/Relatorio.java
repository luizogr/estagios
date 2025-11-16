package com.ufvjm.estagios.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ufvjm.estagios.entities.enums.StatusRelatorio;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_relatorios")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_estagio")
    private Estagio estagio;

    private LocalDate dataPrevistaEntrega;
    private LocalDate dataEntregaRelatorio;

    @Enumerated(EnumType.STRING)
    private StatusRelatorio status;

    public Relatorio() {
    }

    public Relatorio(UUID id, Estagio estagio, LocalDate dataPrevistaEntrega, LocalDate dataEntregaRelatorio, StatusRelatorio status) {
        this.id = id;
        this.estagio = estagio;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.dataEntregaRelatorio = dataEntregaRelatorio;
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

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public LocalDate getDataEntregaRelatorio() {
        return dataEntregaRelatorio;
    }

    public void setDataEntregaRelatorio(LocalDate dataEntregaRelatorio) {
        this.dataEntregaRelatorio = dataEntregaRelatorio;
    }

    public StatusRelatorio getStatus() {
        return status;
    }

    public void setStatus(StatusRelatorio status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Relatorio relatorio = (Relatorio) o;
        return Objects.equals(id, relatorio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
