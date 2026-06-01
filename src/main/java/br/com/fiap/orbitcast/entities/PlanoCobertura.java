package br.com.fiap.orbitcast.entities;

import java.math.BigDecimal;

public class PlanoCobertura {

    private Long id;
    private Long campanhaId;
    private String nome;
    private String descricao;
    private BigDecimal custoTotal;
    private Integer alcanceTotal;
    private String viabilidadeGeral;

    public PlanoCobertura() {
    }

    public PlanoCobertura(Long id, Long campanhaId, String nome, String descricao,
                          BigDecimal custoTotal, Integer alcanceTotal, String viabilidadeGeral) {
        this.id = id;
        this.campanhaId = campanhaId;
        this.nome = nome;
        this.descricao = descricao;
        this.custoTotal = custoTotal;
        this.alcanceTotal = alcanceTotal;
        this.viabilidadeGeral = viabilidadeGeral;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(Long campanhaId) {
        this.campanhaId = campanhaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }

    public Integer getAlcanceTotal() {
        return alcanceTotal;
    }

    public void setAlcanceTotal(Integer alcanceTotal) {
        this.alcanceTotal = alcanceTotal;
    }

    public String getViabilidadeGeral() {
        return viabilidadeGeral;
    }

    public void setViabilidadeGeral(String viabilidadeGeral) {
        this.viabilidadeGeral = viabilidadeGeral;
    }
}
