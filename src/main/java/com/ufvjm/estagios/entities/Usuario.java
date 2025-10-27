package com.ufvjm.estagios.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufvjm.estagios.entities.enums.Role;
import jakarta.persistence.*;

import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    @Column(unique = true)
    private String emailInstitucional;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Usuario() {
    }

    public Usuario(UUID id, String nome, String emailInstitucional, String senha, Role role) {
        this.id = id;
        this.nome = nome;
        this.emailInstitucional = emailInstitucional;
        this.senha = senha;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmailInstitucional() {
        return emailInstitucional;
    }

    public void setEmailInstitucional(String emailInstitucional) {
        this.emailInstitucional = emailInstitucional;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
