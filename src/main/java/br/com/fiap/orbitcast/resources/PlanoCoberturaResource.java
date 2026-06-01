package br.com.fiap.orbitcast.resources;

import br.com.fiap.orbitcast.bo.PlanoCoberturaBo;
import br.com.fiap.orbitcast.entities.PlanoCobertura;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/planos-cobertura")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanoCoberturaResource {

    @Inject
    PlanoCoberturaBo planoBo;

    @GET
    public Response listar() {
        return Response.ok(planoBo.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Response.ok(planoBo.buscarPorId(id)).build();
    }

    @POST
    public Response cadastrar(PlanoCobertura plano) {
        PlanoCobertura criado = planoBo.cadastrar(plano);
        return Response.created(URI.create("/planos-cobertura/" + criado.getId()))
                .entity(criado)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, PlanoCobertura plano) {
        return Response.ok(planoBo.atualizar(id, plano)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        planoBo.remover(id);
        return Response.noContent().build();
    }
}
