package br.com.fiap.orbitcast.resources;

import br.com.fiap.orbitcast.bo.DashboardBo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    DashboardBo dashboardBo;

    @GET
    @Path("/resumo")
    public Response resumo() {
        return Response.ok(dashboardBo.resumo()).build();
    }
}
