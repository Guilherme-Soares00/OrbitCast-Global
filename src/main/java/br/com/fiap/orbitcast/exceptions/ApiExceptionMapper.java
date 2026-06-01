package br.com.fiap.orbitcast.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof EntityNotFoundException) {
            return build(Response.Status.NOT_FOUND, "Recurso nao encontrado", exception.getMessage());
        }

        if (exception instanceof BusinessException) {
            return build(Response.Status.BAD_REQUEST, "Regra de negocio violada", exception.getMessage());
        }

        if (exception instanceof DataAccessException) {
            return build(Response.Status.INTERNAL_SERVER_ERROR, "Erro de banco de dados", exception.getMessage());
        }

        if (exception instanceof WebApplicationException webApplicationException) {
            int status = webApplicationException.getResponse().getStatus();
            return Response.status(status)
                    .entity(new ApiError(status, "Erro HTTP", "Requisicao invalida para este endpoint."))
                    .build();
        }

        return build(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno", "Nao foi possivel processar a requisicao.");
    }

    private Response build(Response.Status status, String erro, String mensagem) {
        return Response.status(status)
                .entity(new ApiError(status.getStatusCode(), erro, mensagem))
                .build();
    }
}
