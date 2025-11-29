package com.ufvjm.estagios.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_estagios")
public class Estagio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "orientador_id")
    private Professor orientador;

    private String concedente;

    private String supervisor;

    private String formacaoSupervisor;

    private LocalDate dataInicio;

    private LocalDate dataTermino;

    private Integer cargaHorariaSemanal;

    @Column(precision = 10, scale = 2) // Ex: 99999999,99
    private BigDecimal valorBolsa;

    private Boolean auxilioTransporte;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorAuxilioTransporte;

    private Boolean seguro;

    @Enumerated(EnumType.STRING)
    private StatusEstagio statusEstagio;

    private  LocalDate dataEntregaTCE; // Conferir se coloca um boolean

    private LocalDate dataEntregaPlanoDeAtividades; // Conferir se coloca boolean

    @JsonManagedReference
    @OneToMany(mappedBy = "estagio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aditivo> aditivos = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "estagio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relatorio> relatorios = new ArrayList<>();

    private Boolean efetivado;

    private String motivoConclusao;

    public Estagio() {
    }

    public Estagio(UUID id, Aluno aluno, Professor orientador, String concedente, String supervisor, LocalDate dataInicio, LocalDate dataTermino, Integer cargaHorariaSemanal, BigDecimal valorBolsa, Boolean auxilioTransporte, BigDecimal valorAuxilioTransporte, Boolean seguro, StatusEstagio statusEstagio, LocalDate dataEntregaTCE, LocalDate dataEntregaPlanoDeAtividades, List<Aditivo> aditivos, List<Relatorio> relatorios) {
        this.id = id;
        this.aluno = aluno;
        this.orientador = orientador;
        this.concedente = concedente;
        this.supervisor = supervisor;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.valorBolsa = valorBolsa;
        this.auxilioTransporte = auxilioTransporte;
        this.valorAuxilioTransporte = valorAuxilioTransporte;
        this.seguro = seguro;
        this.statusEstagio = statusEstagio;
        this.dataEntregaTCE = dataEntregaTCE;
        this.dataEntregaPlanoDeAtividades = dataEntregaPlanoDeAtividades;
        this.aditivos = aditivos;
        this.relatorios = relatorios;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Professor getOrientador() {
        return orientador;
    }

    public void setOrientador(Professor orientador) {
        this.orientador = orientador;
    }

    public String getConcedente() {
        return concedente;
    }

    public void setConcedente(String concedente) {
        this.concedente = concedente;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public Integer getCargaHorariaSemanal() {
        return cargaHorariaSemanal;
    }

    public void setCargaHorariaSemanal(Integer cargaHorariaSemanal) {
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    public BigDecimal getValorBolsa() {
        return valorBolsa;
    }

    public void setValorBolsa(BigDecimal valorBolsa) {
        this.valorBolsa = valorBolsa;
    }

    public Boolean getAuxilioTransporte() {
        return auxilioTransporte;
    }

    public void setAuxilioTransporte(Boolean auxilioTransporte) {
        this.auxilioTransporte = auxilioTransporte;
    }

    public BigDecimal getValorAuxilioTransporte() {
        return valorAuxilioTransporte;
    }

    public void setValorAuxilioTransporte(BigDecimal valorAuxilioTransporte) {
        this.valorAuxilioTransporte = valorAuxilioTransporte;
    }

    public Boolean getSeguro() {
        return seguro;
    }

    public void setSeguro(Boolean seguro) {
        this.seguro = seguro;
    }

    public StatusEstagio getStatusEstagio() {
        return statusEstagio;
    }

    public void setStatusEstagio(StatusEstagio statusEstagio) {
        this.statusEstagio = statusEstagio;
    }

    public LocalDate getDataEntregaTCE() {
        return dataEntregaTCE;
    }

    public void setDataEntregaTCE(LocalDate dataEntregaTCE) {
        this.dataEntregaTCE = dataEntregaTCE;
    }

    public LocalDate getDataEntregaPlanoDeAtividades() {
        return dataEntregaPlanoDeAtividades;
    }

    public void setDataEntregaPlanoDeAtividades(LocalDate dataEntregaPlanoDeAtividades) {
        this.dataEntregaPlanoDeAtividades = dataEntregaPlanoDeAtividades;
    }

    public List<Aditivo> getAditivos() {
        return aditivos;
    }

    public void setAditivos(List<Aditivo> aditivos) {
        this.aditivos = aditivos;
    }

    public List<Relatorio> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(List<Relatorio> relatorios) {
        this.relatorios = relatorios;
    }

    public Boolean getEfetivado() {
        return efetivado;
    }

    public void setEfetivado(Boolean efetivado) {
        this.efetivado = efetivado;
    }

    public String getMotivoConclusao() {
        return motivoConclusao;
    }

    public void setMotivoConclusao(String motivoConclusao) {
        this.motivoConclusao = motivoConclusao;
    }

    public String getFormacaoSupervisor() {
        return formacaoSupervisor;
    }

    public void setFormacaoSupervisor(String formacaoSupervisor) {
        this.formacaoSupervisor = formacaoSupervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Estagio estagio = (Estagio) o;
        return Objects.equals(id, estagio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
