package br.com.fiap.orbitcast.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Simulacao {

    private Long id;
    private Long campanhaId;
    private BigDecimal custoEstimado;
    private Integer alcanceEstimado;
    private BigDecimal qualidadeSinal;
    private String viabilidade;
    private String recomendacao;
    private LocalDateTime dataSimulacao;

    public Simulacao() {
    }

    public Simulacao(Long id, Long campanhaId, BigDecimal custoEstimado, Integer alcanceEstimado,
                     BigDecimal qualidadeSinal, String viabilidade, String recomendacao,
                     LocalDateTime dataSimulacao) {
        this.id = id;
        this.campanhaId = campanhaId;
        this.custoEstimado = custoEstimado;
        this.alcanceEstimado = alcanceEstimado;
        this.qualidadeSinal = qualidadeSinal;
        this.viabilidade = viabilidade;
        this.recomendacao = recomendacao;
        this.dataSimulacao = dataSimulacao;
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

    public BigDecimal getCustoEstimado() {
        return custoEstimado;
    }

    public void setCustoEstimado(BigDecimal custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public Integer getAlcanceEstimado() {
        return alcanceEstimado;
    }

    public void setAlcanceEstimado(Integer alcanceEstimado) {
        this.alcanceEstimado = alcanceEstimado;
    }

    public BigDecimal getQualidadeSinal() {
        return qualidadeSinal;
    }

    public void setQualidadeSinal(BigDecimal qualidadeSinal) {
        this.qualidadeSinal = qualidadeSinal;
    }

    public String getViabilidade() {
        return viabilidade;
    }

    public void setViabilidade(String viabilidade) {
        this.viabilidade = viabilidade;
    }

    public String getRecomendacao() {
        return recomendacao;
    }

    public void setRecomendacao(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }
}
