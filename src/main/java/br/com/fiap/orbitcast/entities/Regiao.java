package br.com.fiap.orbitcast.entities;

import java.math.BigDecimal;

public class Regiao {

    private Long id;
    private String nome;
    private String estado;
    private String pais;
    private Integer populacaoEstimada;
    private BigDecimal indiceConectividade;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal areaKm2;
    private Integer prioridadeSocial;

    public Regiao() {
    }

    public Regiao(Long id, String nome, String estado, String pais, Integer populacaoEstimada,
                  BigDecimal indiceConectividade, BigDecimal latitude, BigDecimal longitude,
                  BigDecimal areaKm2, Integer prioridadeSocial) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.pais = pais;
        this.populacaoEstimada = populacaoEstimada;
        this.indiceConectividade = indiceConectividade;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaKm2 = areaKm2;
        this.prioridadeSocial = prioridadeSocial;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Integer getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    public void setPopulacaoEstimada(Integer populacaoEstimada) {
        this.populacaoEstimada = populacaoEstimada;
    }

    public BigDecimal getIndiceConectividade() {
        return indiceConectividade;
    }

    public void setIndiceConectividade(BigDecimal indiceConectividade) {
        this.indiceConectividade = indiceConectividade;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAreaKm2() {
        return areaKm2;
    }

    public void setAreaKm2(BigDecimal areaKm2) {
        this.areaKm2 = areaKm2;
    }

    public Integer getPrioridadeSocial() {
        return prioridadeSocial;
    }

    public void setPrioridadeSocial(Integer prioridadeSocial) {
        this.prioridadeSocial = prioridadeSocial;
    }
}
