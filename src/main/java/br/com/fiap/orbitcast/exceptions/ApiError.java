package br.com.fiap.orbitcast.exceptions;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String erro;
    private String mensagem;
    private LocalDateTime timestamp;

    public ApiError() {
    }

    public ApiError(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
