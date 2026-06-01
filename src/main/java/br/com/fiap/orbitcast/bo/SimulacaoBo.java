package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CampanhaTransmissaoDao;
import br.com.fiap.orbitcast.dao.SimulacaoDao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.entities.Simulacao;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoBo {

    @Inject
    SimulacaoDao simulacaoDao;

    @Inject
    CampanhaTransmissaoDao campanhaDao;

    public List<Simulacao> listar() {
        return simulacaoDao.listar();
    }

    public Simulacao buscarPorId(Long id) {
        Long simulacaoId = ValidationUtils.requirePositiveId(id, "Id da simulacao deve ser positivo.");
        return simulacaoDao.buscarPorId(simulacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Simulacao nao encontrada."));
    }

    public List<Simulacao> listarPorCampanha(Long campanhaId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        return simulacaoDao.listarPorCampanha(campanha.getId());
    }

    public Simulacao simular(Long campanhaId) {
        CampanhaTransmissao campanha = garantirCampanha(campanhaId);
        if ("CANCELADA".equals(campanha.getStatus()) || "FINALIZADA".equals(campanha.getStatus())) {
            throw new BusinessException("Campanha cancelada ou finalizada nao pode gerar nova simulacao.");
        }

        List<Regiao> regioes = campanhaDao.listarRegioes(campanha.getId());

        if (regioes.isEmpty()) {
            throw new BusinessException("A campanha precisa ter ao menos uma regiao para ser simulada.");
        }

        BigDecimal areaTotal = regioes.stream()
                .map(Regiao::getAreaKm2)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int prioridadeTotal = regioes.stream().mapToInt(Regiao::getPrioridadeSocial).sum();
        int populacaoTotal = regioes.stream().mapToInt(Regiao::getPopulacaoEstimada).sum();
        BigDecimal conectividadeMedia = regioes.stream()
                .map(Regiao::getIndiceConectividade)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(regioes.size()), 2, RoundingMode.HALF_UP);

        BigDecimal custo = calcularCusto(campanha, regioes.size(), areaTotal, prioridadeTotal);
        BigDecimal qualidadeSinal = calcularQualidadeSinal(campanha.getQualidadeDesejada(), areaTotal, prioridadeTotal, regioes.size());
        int alcance = calcularAlcance(populacaoTotal, conectividadeMedia, prioridadeTotal, regioes.size());
        String viabilidade = classificarViabilidade(campanha.getOrcamento(), custo, qualidadeSinal);
        String recomendacao = gerarRecomendacao(viabilidade, campanha.getOrcamento(), custo, regioes.size());

        Simulacao simulacao = new Simulacao();
        simulacao.setCampanhaId(campanha.getId());
        simulacao.setCustoEstimado(custo);
        simulacao.setAlcanceEstimado(alcance);
        simulacao.setQualidadeSinal(qualidadeSinal);
        simulacao.setViabilidade(viabilidade);
        simulacao.setRecomendacao(recomendacao);
        simulacao.setDataSimulacao(LocalDateTime.now());

        return simulacaoDao.inserir(simulacao);
    }

    private CampanhaTransmissao garantirCampanha(Long campanhaId) {
        Long id = ValidationUtils.requirePositiveId(campanhaId, "Id da campanha deve ser positivo.");
        return campanhaDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada."));
    }

    private BigDecimal calcularCusto(CampanhaTransmissao campanha, int quantidadeRegioes,
                                     BigDecimal areaTotal, int prioridadeTotal) {
        BigDecimal custoPorHora = switch (campanha.getQualidadeDesejada()) {
            case "SD" -> BigDecimal.valueOf(250);
            case "HD" -> BigDecimal.valueOf(450);
            case "FULL_HD" -> BigDecimal.valueOf(700);
            case "4K" -> BigDecimal.valueOf(1200);
            default -> BigDecimal.valueOf(450);
        };

        BigDecimal custoDuracao = custoPorHora.multiply(BigDecimal.valueOf(campanha.getDuracaoHoras()));
        BigDecimal custoArea = areaTotal.multiply(BigDecimal.valueOf(8));
        BigDecimal custoRegioes = BigDecimal.valueOf(quantidadeRegioes).multiply(BigDecimal.valueOf(1500));
        BigDecimal custoPrioridade = BigDecimal.valueOf(prioridadeTotal).multiply(BigDecimal.valueOf(500));

        return custoDuracao
                .add(custoArea)
                .add(custoRegioes)
                .add(custoPrioridade)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularQualidadeSinal(String qualidadeDesejada, BigDecimal areaTotal,
                                              int prioridadeTotal, int quantidadeRegioes) {
        BigDecimal base = switch (qualidadeDesejada) {
            case "SD" -> BigDecimal.valueOf(70);
            case "HD" -> BigDecimal.valueOf(78);
            case "FULL_HD" -> BigDecimal.valueOf(86);
            case "4K" -> BigDecimal.valueOf(92);
            default -> BigDecimal.valueOf(78);
        };

        BigDecimal penalidadeArea = areaTotal.divide(BigDecimal.valueOf(20000), 2, RoundingMode.HALF_UP);
        BigDecimal bonusPrioridade = BigDecimal.valueOf(prioridadeTotal)
                .divide(BigDecimal.valueOf(quantidadeRegioes), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(2));

        BigDecimal qualidade = base.subtract(penalidadeArea).add(bonusPrioridade);
        if (qualidade.compareTo(BigDecimal.valueOf(98)) > 0) {
            return BigDecimal.valueOf(98).setScale(2, RoundingMode.HALF_UP);
        }
        if (qualidade.compareTo(BigDecimal.valueOf(45)) < 0) {
            return BigDecimal.valueOf(45).setScale(2, RoundingMode.HALF_UP);
        }
        return qualidade.setScale(2, RoundingMode.HALF_UP);
    }

    private int calcularAlcance(int populacaoTotal, BigDecimal conectividadeMedia,
                                int prioridadeTotal, int quantidadeRegioes) {
        BigDecimal prioridadeMedia = BigDecimal.valueOf(prioridadeTotal)
                .divide(BigDecimal.valueOf(quantidadeRegioes), 2, RoundingMode.HALF_UP);
        BigDecimal baixaConectividade = BigDecimal.valueOf(100).subtract(conectividadeMedia)
                .divide(BigDecimal.valueOf(300), 4, RoundingMode.HALF_UP);
        BigDecimal fator = BigDecimal.valueOf(0.25)
                .add(prioridadeMedia.multiply(BigDecimal.valueOf(0.10)))
                .add(baixaConectividade);

        if (fator.compareTo(BigDecimal.valueOf(0.90)) > 0) {
            fator = BigDecimal.valueOf(0.90);
        }

        return BigDecimal.valueOf(populacaoTotal).multiply(fator).intValue();
    }

    private String classificarViabilidade(BigDecimal orcamento, BigDecimal custo, BigDecimal qualidadeSinal) {
        if (orcamento == null || custo == null || custo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Dados da campanha insuficientes para classificar a viabilidade.");
        }
        BigDecimal coberturaOrcamento = orcamento.divide(custo, 4, RoundingMode.HALF_UP);

        if (coberturaOrcamento.compareTo(BigDecimal.ONE) >= 0
                && qualidadeSinal.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "ALTA";
        }
        if (coberturaOrcamento.compareTo(BigDecimal.valueOf(0.70)) >= 0
                || qualidadeSinal.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "MEDIA";
        }
        return "BAIXA";
    }

    private String gerarRecomendacao(String viabilidade, BigDecimal orcamento, BigDecimal custo, int quantidadeRegioes) {
        return switch (viabilidade) {
            case "ALTA" -> "Campanha recomendada: o orcamento cobre a estimativa e a qualidade de sinal esta adequada.";
            case "MEDIA" -> "Revisar plano: considere reduzir regioes, ajustar qualidade ou ampliar o orcamento antes da aprovacao.";
            default -> "Campanha nao recomendada no formato atual: o custo estimado supera muito o orcamento disponivel para "
                    + quantidadeRegioes + " regiao(oes). Custo estimado: R$ " + custo + ", orcamento: R$ " + orcamento + ".";
        };
    }
}
