package br.com.fiap.orbitcast.resources;

import br.com.fiap.orbitcast.bo.CampanhaTransmissaoBo;
import br.com.fiap.orbitcast.bo.PlanoCoberturaBo;
import br.com.fiap.orbitcast.bo.SimulacaoBo;
import br.com.fiap.orbitcast.entities.CampanhaRegiao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.Simulacao;
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

@Path("/campanhas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CampanhaTransmissaoResource {

    @Inject
    CampanhaTransmissaoBo campanhaBo;

    @Inject
    SimulacaoBo simulacaoBo;

    @Inject
    PlanoCoberturaBo planoBo;

    @GET
    public Response listar() {
        return Response.ok(campanhaBo.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Response.ok(campanhaBo.buscarPorId(id)).build();
    }

    @POST
    public Response cadastrar(CampanhaTransmissao campanha) {
        CampanhaTransmissao criada = campanhaBo.cadastrar(campanha);
        return Response.created(URI.create("/campanhas/" + criada.getId()))
                .entity(criada)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, CampanhaTransmissao campanha) {
        return Response.ok(campanhaBo.atualizar(id, campanha)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        campanhaBo.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/regioes")
    public Response listarRegioes(@PathParam("id") Long id) {
        return Response.ok(campanhaBo.listarRegioes(id)).build();
    }

    @POST
    @Path("/{id}/regioes/{regiaoId}")
    public Response adicionarRegiao(@PathParam("id") Long id,
                                    @PathParam("regiaoId") Long regiaoId,
                                    CampanhaRegiao campanhaRegiao) {
        campanhaBo.adicionarRegiao(id, regiaoId, campanhaRegiao);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}/regioes/{regiaoId}")
    public Response removerRegiao(@PathParam("id") Long id, @PathParam("regiaoId") Long regiaoId) {
        campanhaBo.removerRegiao(id, regiaoId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/simulacoes")
    public Response listarSimulacoes(@PathParam("id") Long id) {
        return Response.ok(simulacaoBo.listarPorCampanha(id)).build();
    }

    @GET
    @Path("/{id}/planos-cobertura")
    public Response listarPlanosCobertura(@PathParam("id") Long id) {
        return Response.ok(planoBo.listarPorCampanha(id)).build();
    }

    @POST
    @Path("/{id}/simulacoes")
    @Consumes(MediaType.WILDCARD)
    public Response simular(@PathParam("id") Long id) {
        Simulacao simulacao = simulacaoBo.simular(id);
        return Response.created(URI.create("/simulacoes/" + simulacao.getId()))
                .entity(simulacao)
                .build();
    }
}
