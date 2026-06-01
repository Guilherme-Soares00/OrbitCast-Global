package br.com.fiap.orbitcast.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardResumo {

    private int totalClientes;
    private int totalCanais;
    private int totalRegioes;
    private int totalCampanhas;
    private int totalSimulacoes;
    private int alcanceEstimadoTotal;
    private BigDecimal custoMedioSimulacoes;
    private BigDecimal qualidadeMediaSinal;
    private Map<String, Integer> campanhasPorStatus;
    private Map<String, Integer> simulacoesPorViabilidade;

    public int getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(int totalClientes) {
        this.totalClientes = totalClientes;
    }

    public int getTotalCanais() {
        return totalCanais;
    }

    public void setTotalCanais(int totalCanais) {
        this.totalCanais = totalCanais;
    }

    public int getTotalRegioes() {
        return totalRegioes;
    }

    public void setTotalRegioes(int totalRegioes) {
        this.totalRegioes = totalRegioes;
    }

    public int getTotalCampanhas() {
        return totalCampanhas;
    }

    public void setTotalCampanhas(int totalCampanhas) {
        this.totalCampanhas = totalCampanhas;
    }

    public int getTotalSimulacoes() {
        return totalSimulacoes;
    }

    public void setTotalSimulacoes(int totalSimulacoes) {
        this.totalSimulacoes = totalSimulacoes;
    }

    public int getAlcanceEstimadoTotal() {
        return alcanceEstimadoTotal;
    }

    public void setAlcanceEstimadoTotal(int alcanceEstimadoTotal) {
        this.alcanceEstimadoTotal = alcanceEstimadoTotal;
    }

    public BigDecimal getCustoMedioSimulacoes() {
        return custoMedioSimulacoes;
    }

    public void setCustoMedioSimulacoes(BigDecimal custoMedioSimulacoes) {
        this.custoMedioSimulacoes = custoMedioSimulacoes;
    }

    public BigDecimal getQualidadeMediaSinal() {
        return qualidadeMediaSinal;
    }

    public void setQualidadeMediaSinal(BigDecimal qualidadeMediaSinal) {
        this.qualidadeMediaSinal = qualidadeMediaSinal;
    }

    public Map<String, Integer> getCampanhasPorStatus() {
        return campanhasPorStatus;
    }

    public void setCampanhasPorStatus(Map<String, Integer> campanhasPorStatus) {
        this.campanhasPorStatus = campanhasPorStatus;
    }

    public Map<String, Integer> getSimulacoesPorViabilidade() {
        return simulacoesPorViabilidade;
    }

    public void setSimulacoesPorViabilidade(Map<String, Integer> simulacoesPorViabilidade) {
        this.simulacoesPorViabilidade = simulacoesPorViabilidade;
    }
}
