package br.com.fiap.orbitcast.entities;

public class CampanhaRegiao {

    private Long campanhaId;
    private Long regiaoId;
    private Integer prioridade;
    private String observacao;

    public CampanhaRegiao() {
    }

    public CampanhaRegiao(Long campanhaId, Long regiaoId, Integer prioridade, String observacao) {
        this.campanhaId = campanhaId;
        this.regiaoId = regiaoId;
        this.prioridade = prioridade;
        this.observacao = observacao;
    }

    public Long getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(Long campanhaId) {
        this.campanhaId = campanhaId;
    }

    public Long getRegiaoId() {
        return regiaoId;
    }

    public void setRegiaoId(Long regiaoId) {
        this.regiaoId = regiaoId;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
