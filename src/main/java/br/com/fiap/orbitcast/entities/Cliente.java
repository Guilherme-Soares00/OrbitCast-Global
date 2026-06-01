package br.com.fiap.orbitcast.entities;

import java.time.LocalDateTime;

public class Cliente {

    private Long id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private String segmento;
    private LocalDateTime dataCadastro;
    private Boolean ativo;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String documento, String email, String telefone,
                   String segmento, LocalDateTime dataCadastro, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
        this.segmento = segmento;
        this.dataCadastro = dataCadastro;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
