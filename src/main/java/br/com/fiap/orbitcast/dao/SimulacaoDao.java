package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.Simulacao;
import br.com.fiap.orbitcast.exceptions.DataAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SimulacaoDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<Simulacao> listar() {
        String sql = "SELECT * FROM simulacoes ORDER BY id";
        List<Simulacao> simulacoes = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                simulacoes.add(mapear(resultSet));
            }
            return simulacoes;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar simulacoes.", exception);
        }
    }

    public Optional<Simulacao> buscarPorId(Long id) {
        String sql = "SELECT * FROM simulacoes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapear(resultSet));
                }
                return Optional.empty();
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao buscar simulacao.", exception);
        }
    }

    public List<Simulacao> listarPorCampanha(Long campanhaId) {
        String sql = "SELECT * FROM simulacoes WHERE campanha_id = ? ORDER BY data_simulacao DESC";
        List<Simulacao> simulacoes = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    simulacoes.add(mapear(resultSet));
                }
            }

            return simulacoes;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar simulacoes da campanha.", exception);
        }
    }

    public Simulacao inserir(Simulacao simulacao) {
        String sql = """
                INSERT INTO simulacoes
                (campanha_id, custo_estimado, alcance_estimado, qualidade_sinal,
                 viabilidade, recomendacao, data_simulacao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            simulacao.setDataSimulacao(simulacao.getDataSimulacao() == null ? LocalDateTime.now() : simulacao.getDataSimulacao());

            statement.setLong(1, simulacao.getCampanhaId());
            statement.setBigDecimal(2, simulacao.getCustoEstimado());
            statement.setInt(3, simulacao.getAlcanceEstimado());
            statement.setBigDecimal(4, simulacao.getQualidadeSinal());
            statement.setString(5, simulacao.getViabilidade());
            statement.setString(6, simulacao.getRecomendacao());
            statement.setTimestamp(7, Timestamp.valueOf(simulacao.getDataSimulacao()));
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    simulacao.setId(keys.getLong(1));
                }
            }

            return simulacao;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir simulacao.", exception);
        }
    }

    private Simulacao mapear(ResultSet resultSet) throws Exception {
        return new Simulacao(
                resultSet.getLong("id"),
                resultSet.getLong("campanha_id"),
                resultSet.getBigDecimal("custo_estimado"),
                resultSet.getInt("alcance_estimado"),
                resultSet.getBigDecimal("qualidade_sinal"),
                resultSet.getString("viabilidade"),
                resultSet.getString("recomendacao"),
                resultSet.getTimestamp("data_simulacao").toLocalDateTime()
        );
    }
}
