package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.DashboardDao;
import br.com.fiap.orbitcast.dto.DashboardResumo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DashboardBo {

    @Inject
    DashboardDao dashboardDao;

    public DashboardResumo resumo() {
        return dashboardDao.resumo();
    }
}
