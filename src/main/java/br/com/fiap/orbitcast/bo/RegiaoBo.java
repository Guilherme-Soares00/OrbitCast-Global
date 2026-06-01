package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.RegiaoDao;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class RegiaoBo {

    @Inject
    RegiaoDao regiaoDao;

    public List<Regiao> listar() {
        return regiaoDao.listar();
    }

    public Regiao buscarPorId(Long id) {
        Long regiaoId = ValidationUtils.requirePositiveId(id, "Id da regiao deve ser positivo.");
        return regiaoDao.buscarPorId(regiaoId)
                .orElseThrow(() -> new EntityNotFoundException("Regiao nao encontrada."));
    }

    public Regiao cadastrar(Regiao regiao) {
        validar(regiao, null);
        return regiaoDao.inserir(regiao);
    }

    public Regiao atualizar(Long id, Regiao regiao) {
        Long regiaoId = ValidationUtils.requirePositiveId(id, "Id da regiao deve ser positivo.");
        if (!regiaoDao.existe(regiaoId)) {
            throw new EntityNotFoundException("Regiao nao encontrada.");
        }
        validar(regiao, regiaoId);
        regiaoDao.atualizar(regiaoId, regiao);
        return buscarPorId(regiaoId);
    }

    public void remover(Long id) {
        Long regiaoId = ValidationUtils.requirePositiveId(id, "Id da regiao deve ser positivo.");
        if (!regiaoDao.existe(regiaoId)) {
            throw new EntityNotFoundException("Regiao nao encontrada.");
        }
        if (regiaoDao.estaAssociadaCampanha(regiaoId)) {
            throw new BusinessException("Regiao possui campanhas vinculadas.");
        }
        regiaoDao.remover(regiaoId);
    }

    private void validar(Regiao regiao, Long idIgnorado) {
        if (regiao == null) {
            throw new BusinessException("Regiao deve ser informada.");
        }
        regiao.setNome(ValidationUtils.requireText(regiao.getNome(), "Nome da regiao", 120));
        regiao.setEstado(normalizarEstado(regiao.getEstado()));
        regiao.setPais(ValidationUtils.requireText(regiao.getPais(), "Pais", 80));
        validarCoordenadas(regiao);
        validarNumeros(regiao);

        if (regiaoDao.nomeEstadoPaisExiste(regiao.getNome(), regiao.getEstado(), regiao.getPais(), idIgnorado)) {
            throw new BusinessException("Regiao ja cadastrada para este estado e pais.");
        }
    }

    private String normalizarEstado(String estado) {
        String valor = ValidationUtils.requireText(estado, "Estado", 2).toUpperCase();
        if (!valor.matches("[A-Z]{2}")) {
            throw new BusinessException("Estado deve conter a sigla com duas letras.");
        }
        return valor;
    }

    private void validarCoordenadas(Regiao regiao) {
        if ((regiao.getLatitude() == null && regiao.getLongitude() != null)
                || (regiao.getLatitude() != null && regiao.getLongitude() == null)) {
            throw new BusinessException("Latitude e longitude devem ser informadas em conjunto.");
        }
        if (regiao.getLatitude() != null) {
            ValidationUtils.requireBetween(regiao.getLatitude(), "Latitude", BigDecimal.valueOf(-90), BigDecimal.valueOf(90));
        }
        if (regiao.getLongitude() != null) {
            ValidationUtils.requireBetween(regiao.getLongitude(), "Longitude", BigDecimal.valueOf(-180), BigDecimal.valueOf(180));
        }
    }

    private void validarNumeros(Regiao regiao) {
        if (regiao.getPopulacaoEstimada() == null || regiao.getPopulacaoEstimada() < 0) {
            throw new BusinessException("Populacao estimada deve ser maior ou igual a zero.");
        }
        ValidationUtils.requireBetween(regiao.getIndiceConectividade(), "Indice de conectividade", BigDecimal.ZERO, BigDecimal.valueOf(100));
        ValidationUtils.requireBetween(regiao.getPrioridadeSocial(), "Prioridade social", 1, 5);
        ValidationUtils.requirePositive(regiao.getAreaKm2(), "Area em km2");
    }
}
