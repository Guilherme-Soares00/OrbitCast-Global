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
            resumo.setTotalClientes(contar(connection, "T_OC_CLIENTE"));
            resumo.setTotalCanais(contar(connection, "T_OC_CANAL"));
            resumo.setTotalRegioes(contar(connection, "T_OC_REGIAO"));
            resumo.setTotalCampanhas(contar(connection, "T_OC_CAMPANHA"));
            resumo.setTotalSimulacoes(contar(connection, "T_OC_SIMULACAO"));
            resumo.setCampanhasPorStatus(agrupar(connection, "T_OC_CAMPANHA", "status"));
            resumo.setSimulacoesPorViabilidade(agrupar(connection, "T_OC_SIMULACAO", "viabilidade"));
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
            return lerInteiro(resultSet, 1);
        }
    }

    private Map<String, Integer> agrupar(Connection connection, String tabela, String coluna) throws Exception {
        String sql = "SELECT " + coluna + ", COUNT(*) FROM " + tabela + " GROUP BY " + coluna + " ORDER BY " + coluna;
        Map<String, Integer> dados = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                dados.put(resultSet.getString(1), lerInteiro(resultSet, 2));
            }
        }

        return dados;
    }

    private void preencherMetricasSimulacao(Connection connection, DashboardResumo resumo) throws Exception {
        String sql = """
                SELECT COALESCE(SUM(alcance_estimado), 0),
                       COALESCE(AVG(custo_estimado), 0),
                       COALESCE(AVG(qualidade_sinal), 0)
                  FROM T_OC_SIMULACAO
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            resumo.setAlcanceEstimadoTotal(lerInteiro(resultSet, 1));
            resumo.setCustoMedioSimulacoes(lerDecimal(resultSet, 2));
            resumo.setQualidadeMediaSinal(lerDecimal(resultSet, 3));
        }

        if (resumo.getCustoMedioSimulacoes() == null) {
            resumo.setCustoMedioSimulacoes(BigDecimal.ZERO);
        }
        if (resumo.getQualidadeMediaSinal() == null) {
            resumo.setQualidadeMediaSinal(BigDecimal.ZERO);
        }
    }

    private int lerInteiro(ResultSet resultSet, int coluna) throws Exception {
        Object valor = resultSet.getObject(coluna);

        if (valor == null) {
            return 0;
        }
        if (valor instanceof Number numero) {
            return numero.intValue();
        }

        return Integer.parseInt(valor.toString());
    }

    private BigDecimal lerDecimal(ResultSet resultSet, int coluna) throws Exception {
        Object valor = resultSet.getObject(coluna);

        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }
        if (valor instanceof Number numero) {
            return BigDecimal.valueOf(numero.doubleValue());
        }

        return new BigDecimal(valor.toString());
    }
}
