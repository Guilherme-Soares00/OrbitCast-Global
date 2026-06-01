package br.com.fiap.orbitcast.resources;

import br.com.fiap.orbitcast.bo.SimulacaoBo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoBo simulacaoBo;

    @GET
    public Response listar() {
        return Response.ok(simulacaoBo.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Response.ok(simulacaoBo.buscarPorId(id)).build();
    }
}
