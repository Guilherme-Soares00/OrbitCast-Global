package br.com.fiap.orbitcast.resources;

import br.com.fiap.orbitcast.bo.RegiaoBo;
import br.com.fiap.orbitcast.entities.Regiao;
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

@Path("/regioes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegiaoResource {

    @Inject
    RegiaoBo regiaoBo;

    @GET
    public Response listar() {
        return Response.ok(regiaoBo.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Response.ok(regiaoBo.buscarPorId(id)).build();
    }

    @POST
    public Response cadastrar(Regiao regiao) {
        Regiao criada = regiaoBo.cadastrar(regiao);
        return Response.created(URI.create("/regioes/" + criada.getId()))
                .entity(criada)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, Regiao regiao) {
        return Response.ok(regiaoBo.atualizar(id, regiao)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        regiaoBo.remover(id);
        return Response.noContent().build();
    }
}
