package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.dto.DashboardResumo;
import br.com.fiap.orbitcast.exceptions.DataAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class DashboardDao {

    @Inject
    DatabaseConnection databaseConnection;

    public DashboardResumo resumo() {
        try (Connection connection = databaseConnection.getConnection()) {
            DashboardResumo resumo = new DashboardResumo();
            resumo.setTotalClientes(contar(connection, "clientes"));
            resumo.setTotalCanais(contar(connection, "canais"));
            resumo.setTotalRegioes(contar(connection, "regioes"));
            resumo.setTotalCampanhas(contar(connection, "campanhas_transmissao"));
            resumo.setTotalSimulacoes(contar(connection, "simulacoes"));
            resumo.setCampanhasPorStatus(agrupar(connection, "campanhas_transmissao", "status"));
            resumo.setSimulacoesPorViabilidade(agrupar(connection, "simulacoes", "viabilidade"));
            preencherMetricasSimulacao(connection, resumo);
            return resumo;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao carregar resumo do dashboard.", exception);
        }
    }

    private int contar(Connection connection, String tabela) throws Exception {
        String sql = "SELECT COUNT(*) FROM " + tabela;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private Map<String, Integer> agrupar(Connection connection, String tabela, String coluna) throws Exception {
        String sql = "SELECT " + coluna + ", COUNT(*) FROM " + tabela + " GROUP BY " + coluna + " ORDER BY " + coluna;
        Map<String, Integer> dados = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                dados.put(resultSet.getString(1), resultSet.getInt(2));
            }
        }

        return dados;
    }

    private void preencherMetricasSimulacao(Connection connection, DashboardResumo resumo) throws Exception {
        String sql = """
                SELECT SUM(alcance_estimado),
                       AVG(custo_estimado),
                       AVG(qualidade_sinal)
                  FROM simulacoes
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            resumo.setAlcanceEstimadoTotal(resultSet.getInt(1));
            resumo.setCustoMedioSimulacoes(resultSet.getBigDecimal(2));
            resumo.setQualidadeMediaSinal(resultSet.getBigDecimal(3));
        }

        if (resumo.getCustoMedioSimulacoes() == null) {
            resumo.setCustoMedioSimulacoes(BigDecimal.ZERO);
        }
        if (resumo.getQualidadeMediaSinal() == null) {
            resumo.setQualidadeMediaSinal(BigDecimal.ZERO);
        }
    }
}
