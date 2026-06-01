package br.com.fiap.orbitcast.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CampanhaTransmissao {

    private Long id;
    private Long clienteId;
    private Long canalId;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer duracaoHoras;
    private String qualidadeDesejada;
    private BigDecimal orcamento;
    private String status;

    public CampanhaTransmissao() {
    }

    public CampanhaTransmissao(Long id, Long clienteId, Long canalId, String nome, String descricao,
                               LocalDate dataInicio, LocalDate dataFim, Integer duracaoHoras,
                               String qualidadeDesejada, BigDecimal orcamento, String status) {
        this.id = id;
        this.clienteId = clienteId;
        this.canalId = canalId;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.duracaoHoras = duracaoHoras;
        this.qualidadeDesejada = qualidadeDesejada;
        this.orcamento = orcamento;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getCanalId() {
        return canalId;
    }

    public void setCanalId(Long canalId) {
        this.canalId = canalId;
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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getDuracaoHoras() {
        return duracaoHoras;
    }

    public void setDuracaoHoras(Integer duracaoHoras) {
        this.duracaoHoras = duracaoHoras;
    }

    public String getQualidadeDesejada() {
        return qualidadeDesejada;
    }

    public void setQualidadeDesejada(String qualidadeDesejada) {
        this.qualidadeDesejada = qualidadeDesejada;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
