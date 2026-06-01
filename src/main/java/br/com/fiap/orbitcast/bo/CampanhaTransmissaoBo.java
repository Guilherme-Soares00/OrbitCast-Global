package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CampanhaTransmissaoDao;
import br.com.fiap.orbitcast.dao.CanalDao;
import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.dao.RegiaoDao;
import br.com.fiap.orbitcast.entities.CampanhaRegiao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.Canal;
import br.com.fiap.orbitcast.entities.Cliente;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CampanhaTransmissaoBo {

    private static final Set<String> QUALIDADES = Set.of("SD", "HD", "FULL_HD", "4K");
    private static final Set<String> STATUS = Set.of("PLANEJADA", "EM_ANALISE", "APROVADA", "CANCELADA", "FINALIZADA");

    @Inject
    CampanhaTransmissaoDao campanhaDao;

    @Inject
    ClienteDao clienteDao;

    @Inject
    CanalDao canalDao;

    @Inject
    RegiaoDao regiaoDao;

    public List<CampanhaTransmissao> listar() {
        return campanhaDao.listar();
    }

    public CampanhaTransmissao buscarPorId(Long id) {
        Long campanhaId = ValidationUtils.requirePositiveId(id, "Id da campanha deve ser positivo.");
        return campanhaDao.buscarPorId(campanhaId)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada."));
    }

    public List<Regiao> listarRegioes(Long campanhaId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        return campanhaDao.listarRegioes(campanha.getId());
    }

    public CampanhaTransmissao cadastrar(CampanhaTransmissao campanha) {
        validar(campanha);
        if (campanhaEncerrada(campanha.getStatus())) {
            throw new BusinessException("Campanha nova nao pode iniciar cancelada ou finalizada.");
        }
        return campanhaDao.inserir(campanha);
    }

    public CampanhaTransmissao atualizar(Long id, CampanhaTransmissao campanha) {
        Long campanhaId = ValidationUtils.requirePositiveId(id, "Id da campanha deve ser positivo.");
        CampanhaTransmissao existente = campanhaDao.buscarPorId(campanhaId)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada."));
        garantirCampanhaEditavel(existente);
        validar(campanha);
        if ("APROVADA".equals(campanha.getStatus()) && !campanhaDao.possuiRegioes(campanhaId)) {
            throw new BusinessException("Campanha aprovada precisa ter ao menos uma regiao associada.");
        }
        campanhaDao.atualizar(campanhaId, campanha);
        return buscarPorId(campanhaId);
    }

    public void remover(Long id) {
        Long campanhaId = ValidationUtils.requirePositiveId(id, "Id da campanha deve ser positivo.");
        if (!campanhaDao.remover(campanhaId)) {
            throw new EntityNotFoundException("Campanha nao encontrada.");
        }
    }

    public void adicionarRegiao(Long campanhaId, Long regiaoId, CampanhaRegiao request) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        garantirCampanhaEditavel(campanha);
        Long regiao = ValidationUtils.requirePositiveId(regiaoId, "Id da regiao deve ser positivo.");
        if (!regiaoDao.existe(regiao)) {
            throw new BusinessException("Regiao informada nao existe.");
        }
        if (campanhaDao.campanhaPossuiRegiao(campanha.getId(), regiao)) {
            throw new BusinessException("Regiao ja esta associada a campanha.");
        }

        int prioridade = request == null || request.getPrioridade() == null ? 3 : request.getPrioridade();
        prioridade = ValidationUtils.requireBetween(prioridade, "Prioridade da regiao na campanha", 1, 5);

        String observacao = request == null ? null : ValidationUtils.optionalText(request.getObservacao(), "Observacao", 300);
        campanhaDao.adicionarRegiao(new CampanhaRegiao(campanha.getId(), regiao, prioridade, observacao));
    }

    public void removerRegiao(Long campanhaId, Long regiaoId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        garantirCampanhaEditavel(campanha);
        Long regiao = ValidationUtils.requirePositiveId(regiaoId, "Id da regiao deve ser positivo.");
        if (!campanhaDao.removerRegiao(campanha.getId(), regiao)) {
            throw new EntityNotFoundException("Associacao entre campanha e regiao nao encontrada.");
        }
    }

    private void validar(CampanhaTransmissao campanha) {
        if (campanha == null) {
            throw new BusinessException("Campanha deve ser informada.");
        }
        Long clienteId = ValidationUtils.requirePositiveId(campanha.getClienteId(), "Id do cliente deve ser positivo.");
        Cliente cliente = clienteDao.buscarPorId(clienteId)
                .orElseThrow(() -> new BusinessException("Cliente informado para a campanha nao existe."));
        if (!Boolean.TRUE.equals(cliente.getAtivo())) {
            throw new BusinessException("Cliente informado para a campanha deve estar ativo.");
        }

        Long canalId = ValidationUtils.requirePositiveId(campanha.getCanalId(), "Id do canal deve ser positivo.");
        Canal canal = canalDao.buscarPorId(canalId)
                .orElseThrow(() -> new BusinessException("Canal informado para a campanha nao existe."));
        if (!Boolean.TRUE.equals(canal.getAtivo())) {
            throw new BusinessException("Canal informado para a campanha deve estar ativo.");
        }
        if (!canal.getClienteId().equals(clienteId)) {
            throw new BusinessException("Canal informado nao pertence ao cliente da campanha.");
        }

        campanha.setClienteId(clienteId);
        campanha.setCanalId(canalId);
        campanha.setNome(ValidationUtils.requireText(campanha.getNome(), "Nome da campanha", 140));
        campanha.setDescricao(ValidationUtils.optionalText(campanha.getDescricao(), "Descricao", 500));
        if (campanha.getDataInicio() == null || campanha.getDataFim() == null) {
            throw new BusinessException("Datas de inicio e fim sao obrigatorias.");
        }
        if (campanha.getDataInicio().isAfter(campanha.getDataFim())) {
            throw new BusinessException("Data de inicio nao pode ser posterior a data de fim.");
        }
        if (campanha.getDuracaoHoras() == null || campanha.getDuracaoHoras() <= 0) {
            throw new BusinessException("Duracao em horas deve ser maior que zero.");
        }
        long horasDisponiveis = (ChronoUnit.DAYS.between(campanha.getDataInicio(), campanha.getDataFim()) + 1) * 24;
        if (campanha.getDuracaoHoras() > horasDisponiveis) {
            throw new BusinessException("Duracao em horas nao pode exceder o periodo da campanha.");
        }
        campanha.setQualidadeDesejada(normalizarQualidade(campanha.getQualidadeDesejada()));
        campanha.setStatus(normalizarStatus(campanha.getStatus()));
        if (campanha.getOrcamento() == null || campanha.getOrcamento().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Orcamento deve ser maior ou igual a zero.");
        }
    }

    private CampanhaTransmissao garantirCampanha(Long campanhaId) {
        Long id = ValidationUtils.requirePositiveId(campanhaId, "Id da campanha deve ser positivo.");
        return campanhaDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada."));
    }

    private void garantirCampanhaEditavel(CampanhaTransmissao campanha) {
        if (campanhaEncerrada(campanha.getStatus())) {
            throw new BusinessException("Campanha cancelada ou finalizada nao pode ser alterada.");
        }
    }

    private boolean campanhaEncerrada(String status) {
        return "CANCELADA".equals(status) || "FINALIZADA".equals(status);
    }

    private String normalizarQualidade(String qualidade) {
        String valor = ValidationUtils.requireText(qualidade, "Qualidade desejada", 20).toUpperCase();
        if (!QUALIDADES.contains(valor)) {
            throw new BusinessException("Qualidade desejada deve ser SD, HD, FULL_HD ou 4K.");
        }
        return valor;
    }

    private String normalizarStatus(String status) {
        String valorInformado = ValidationUtils.normalizeText(status);
        if (valorInformado == null) {
            return "PLANEJADA";
        }
        String valor = valorInformado.toUpperCase();
        if (!STATUS.contains(valor)) {
            throw new BusinessException("Status da campanha invalido.");
        }
        return valor;
    }
}
