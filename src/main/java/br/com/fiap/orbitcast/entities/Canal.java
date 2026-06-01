package br.com.fiap.orbitcast.entities;

public class Canal {

    private Long id;
    private Long clienteId;
    private String nome;
    private String tipoConteudo;
    private String publicoAlvo;
    private String classificacaoIndicativa;
    private Boolean ativo;

    public Canal() {
    }

    public Canal(Long id, Long clienteId, String nome, String tipoConteudo, String publicoAlvo,
                 String classificacaoIndicativa, Boolean ativo) {
        this.id = id;
        this.clienteId = clienteId;
        this.nome = nome;
        this.tipoConteudo = tipoConteudo;
        this.publicoAlvo = publicoAlvo;
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.ativo = ativo;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public String getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(String publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }

    public String getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(String classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
