package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CampanhaTransmissaoDao;
import br.com.fiap.orbitcast.dao.PlanoCoberturaDao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.PlanoCobertura;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PlanoCoberturaBo {

    private static final Set<String> VIABILIDADES = Set.of("BAIXA", "MEDIA", "ALTA");

    @Inject
    PlanoCoberturaDao planoDao;

    @Inject
    CampanhaTransmissaoDao campanhaDao;

    public List<PlanoCobertura> listar() {
        return planoDao.listar();
    }

    public PlanoCobertura buscarPorId(Long id) {
        Long planoId = ValidationUtils.requirePositiveId(id, "Id do plano de cobertura deve ser positivo.");
        return planoDao.buscarPorId(planoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano de cobertura nao encontrado."));
    }

    public List<PlanoCobertura> listarPorCampanha(Long campanhaId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        return planoDao.listarPorCampanha(campanha.getId());
    }

    public PlanoCobertura cadastrar(PlanoCobertura plano) {
        validar(plano, null);
        return planoDao.inserir(plano);
    }

    public PlanoCobertura atualizar(Long id, PlanoCobertura plano) {
        Long planoId = ValidationUtils.requirePositiveId(id, "Id do plano de cobertura deve ser positivo.");
        PlanoCobertura existente = planoDao.buscarPorId(planoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano de cobertura nao encontrado."));
        garantirCampanhaEditavel(existente.getCampanhaId());
        validar(plano, planoId);
        planoDao.atualizar(planoId, plano);
        return buscarPorId(planoId);
    }

    public void remover(Long id) {
        Long planoId = ValidationUtils.requirePositiveId(id, "Id do plano de cobertura deve ser positivo.");
        PlanoCobertura existente = planoDao.buscarPorId(planoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano de cobertura nao encontrado."));
        garantirCampanhaEditavel(existente.getCampanhaId());
        planoDao.remover(planoId);
    }

    private void validar(PlanoCobertura plano, Long idIgnorado) {
        if (plano == null) {
            throw new BusinessException("Plano de cobertura deve ser informado.");
        }

        CampanhaTransmissao campanha = garantirCampanha(plano.getCampanhaId());
        garantirCampanhaEditavel(campanha);
        if (!campanhaDao.possuiRegioes(campanha.getId())) {
            throw new BusinessException("Plano de cobertura exige campanha com ao menos uma regiao associada.");
        }

        plano.setCampanhaId(campanha.getId());
        plano.setNome(ValidationUtils.requireText(plano.getNome(), "Nome do plano de cobertura", 120));
        plano.setDescricao(ValidationUtils.optionalText(plano.getDescricao(), "Descricao", 500));
        if (plano.getCustoTotal() == null || plano.getCustoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Custo total do plano deve ser maior que zero.");
        }
        if (plano.getAlcanceTotal() == null || plano.getAlcanceTotal() <= 0) {
            throw new BusinessException("Alcance total do plano deve ser maior que zero.");
        }
        plano.setViabilidadeGeral(normalizarViabilidade(plano.getViabilidadeGeral()));

        if (planoDao.nomeExisteParaCampanha(plano.getNome(), plano.getCampanhaId(), idIgnorado)) {
            throw new BusinessException("Campanha ja possui um plano de cobertura com este nome.");
        }
    }

    private CampanhaTransmissao garantirCampanha(Long campanhaId) {
        Long id = ValidationUtils.requirePositiveId(campanhaId, "Id da campanha deve ser positivo.");
        return campanhaDao.buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Campanha informada para o plano nao existe."));
    }

    private void garantirCampanhaEditavel(Long campanhaId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        garantirCampanhaEditavel(campanha);
    }

    private void garantirCampanhaEditavel(CampanhaTransmissao campanha) {
        if ("CANCELADA".equals(campanha.getStatus()) || "FINALIZADA".equals(campanha.getStatus())) {
            throw new BusinessException("Campanha cancelada ou finalizada nao pode alterar planos de cobertura.");
        }
    }

    private String normalizarViabilidade(String viabilidade) {
        String valor = ValidationUtils.requireText(viabilidade, "Viabilidade geral", 20).toUpperCase();
        if (!VIABILIDADES.contains(valor)) {
            throw new BusinessException("Viabilidade geral deve ser BAIXA, MEDIA ou ALTA.");
        }
        return valor;
    }
}
