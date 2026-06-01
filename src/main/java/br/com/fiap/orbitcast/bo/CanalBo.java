package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CanalDao;
import br.com.fiap.orbitcast.dao.CampanhaTransmissaoDao;
import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.entities.Canal;
import br.com.fiap.orbitcast.entities.Cliente;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CanalBo {

    @Inject
    CanalDao canalDao;

    @Inject
    ClienteDao clienteDao;

    @Inject
    CampanhaTransmissaoDao campanhaDao;

    public List<Canal> listar() {
        return canalDao.listar();
    }

    public Canal buscarPorId(Long id) {
        Long canalId = ValidationUtils.requirePositiveId(id, "Id do canal deve ser positivo.");
        return canalDao.buscarPorId(canalId)
                .orElseThrow(() -> new EntityNotFoundException("Canal nao encontrado."));
    }

    public Canal cadastrar(Canal canal) {
        validar(canal, null);
        return canalDao.inserir(canal);
    }

    public Canal atualizar(Long id, Canal canal) {
        Long canalId = ValidationUtils.requirePositiveId(id, "Id do canal deve ser positivo.");
        Canal existente = canalDao.buscarPorId(canalId)
                .orElseThrow(() -> new EntityNotFoundException("Canal nao encontrado."));
        validar(canal, canalId);
        if (campanhaDao.existePorCanal(canalId) && !existente.getClienteId().equals(canal.getClienteId())) {
            throw new BusinessException("Canal com campanhas vinculadas nao pode trocar de cliente.");
        }
        canalDao.atualizar(canalId, canal);
        return buscarPorId(canalId);
    }

    public void remover(Long id) {
        Long canalId = ValidationUtils.requirePositiveId(id, "Id do canal deve ser positivo.");
        if (!canalDao.existe(canalId)) {
            throw new EntityNotFoundException("Canal nao encontrado.");
        }
        if (campanhaDao.existePorCanal(canalId)) {
            throw new BusinessException("Canal possui campanhas vinculadas.");
        }
        canalDao.remover(canalId);
    }

    private void validar(Canal canal, Long idIgnorado) {
        if (canal == null) {
            throw new BusinessException("Canal deve ser informado.");
        }
        Long clienteId = ValidationUtils.requirePositiveId(canal.getClienteId(), "Id do cliente deve ser positivo.");
        Cliente cliente = clienteDao.buscarPorId(clienteId)
                .orElseThrow(() -> new BusinessException("Cliente informado para o canal nao existe."));
        if (!Boolean.TRUE.equals(cliente.getAtivo())) {
            throw new BusinessException("Cliente informado para o canal deve estar ativo.");
        }
        canal.setClienteId(clienteId);
        canal.setNome(ValidationUtils.requireText(canal.getNome(), "Nome do canal", 120));
        canal.setTipoConteudo(ValidationUtils.requireText(canal.getTipoConteudo(), "Tipo de conteudo", 80));
        canal.setPublicoAlvo(ValidationUtils.requireText(canal.getPublicoAlvo(), "Publico-alvo", 120));
        canal.setClassificacaoIndicativa(ValidationUtils.optionalText(canal.getClassificacaoIndicativa(), "Classificacao indicativa", 20));
        canal.setAtivo(canal.getAtivo() == null || canal.getAtivo());

        if (canalDao.nomeExisteParaCliente(canal.getNome(), canal.getClienteId(), idIgnorado)) {
            throw new BusinessException("Cliente ja possui um canal com este nome.");
        }
    }
}
