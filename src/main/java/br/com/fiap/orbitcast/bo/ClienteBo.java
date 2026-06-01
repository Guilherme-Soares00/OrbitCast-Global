package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.entities.Cliente;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ClienteBo {

    @Inject
    ClienteDao clienteDao;

    public List<Cliente> listar() {
        return clienteDao.listar();
    }

    public Cliente buscarPorId(Long id) {
        Long clienteId = ValidationUtils.requirePositiveId(id, "Id do cliente deve ser positivo.");
        return clienteDao.buscarPorId(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado."));
    }

    public Cliente cadastrar(Cliente cliente) {
        validar(cliente, null);
        return clienteDao.inserir(cliente);
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        Long clienteId = ValidationUtils.requirePositiveId(id, "Id do cliente deve ser positivo.");
        if (!clienteDao.existe(clienteId)) {
            throw new EntityNotFoundException("Cliente nao encontrado.");
        }
        validar(cliente, clienteId);
        clienteDao.atualizar(clienteId, cliente);
        return buscarPorId(clienteId);
    }

    public void remover(Long id) {
        Long clienteId = ValidationUtils.requirePositiveId(id, "Id do cliente deve ser positivo.");
        if (!clienteDao.existe(clienteId)) {
            throw new EntityNotFoundException("Cliente nao encontrado.");
        }
        if (clienteDao.possuiCanais(clienteId) || clienteDao.possuiCampanhas(clienteId)) {
            throw new BusinessException("Cliente possui canais ou campanhas vinculadas.");
        }
        clienteDao.remover(clienteId);
    }

    private void validar(Cliente cliente, Long idIgnorado) {
        if (cliente == null) {
            throw new BusinessException("Cliente deve ser informado.");
        }
        cliente.setNome(ValidationUtils.requireText(cliente.getNome(), "Nome do cliente", 120));
        cliente.setDocumento(ValidationUtils.optionalCpfCnpj(cliente.getDocumento()));
        cliente.setEmail(ValidationUtils.requireEmail(cliente.getEmail()));
        cliente.setTelefone(ValidationUtils.optionalPhone(cliente.getTelefone()));
        cliente.setSegmento(ValidationUtils.requireText(cliente.getSegmento(), "Segmento do cliente", 80));
        cliente.setAtivo(cliente.getAtivo() == null || cliente.getAtivo());

        if (cliente.getDocumento() != null && clienteDao.documentoExiste(cliente.getDocumento(), idIgnorado)) {
            throw new BusinessException("Documento ja esta cadastrado para outro cliente.");
        }
        if (clienteDao.emailExiste(cliente.getEmail(), idIgnorado)) {
            throw new BusinessException("Email ja esta cadastrado para outro cliente.");
        }
    }
}
