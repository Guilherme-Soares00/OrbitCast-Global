package br.com.fiap.orbitcast.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(ApiExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof EntityNotFoundException) {
            logWarn(Response.Status.NOT_FOUND.getStatusCode(), exception);
            return build(Response.Status.NOT_FOUND, "Recurso nao encontrado", exception.getMessage());
        }

        if (exception instanceof BusinessException) {
            logWarn(Response.Status.BAD_REQUEST.getStatusCode(), exception);
            return build(Response.Status.BAD_REQUEST, "Regra de negocio violada", exception.getMessage());
        }

        if (exception instanceof DataAccessException) {
            logError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception);
            return build(Response.Status.INTERNAL_SERVER_ERROR, "Erro de banco de dados", exception.getMessage());
        }

        if (exception instanceof WebApplicationException webApplicationException) {
            int status = webApplicationException.getResponse().getStatus();
            logWarn(status, exception);
            return Response.status(status)
                    .entity(new ApiError(status, "Erro HTTP", "Requisicao invalida para este endpoint."))
                    .build();
        }

        logError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception);
        return build(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno", "Nao foi possivel processar a requisicao.");
    }

    private void logWarn(int status, Throwable exception) {
        LOGGER.warnf(exception, "Falha tratada pela API | status=%d | tipo=%s | motivo=%s",
                status, exception.getClass().getSimpleName(), exception.getMessage());
    }

    private void logError(int status, Throwable exception) {
        LOGGER.errorf(exception, "Falha interna da API | status=%d | tipo=%s | motivo=%s",
                status, exception.getClass().getSimpleName(), exception.getMessage());
    }

    private Response build(Response.Status status, String erro, String mensagem) {
        return Response.status(status)
                .entity(new ApiError(status.getStatusCode(), erro, mensagem))
                .build();
    }
}
