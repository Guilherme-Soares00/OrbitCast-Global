package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.PlanoCobertura;
import br.com.fiap.orbitcast.exceptions.DataAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PlanoCoberturaDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<PlanoCobertura> listar() {
        String sql = "SELECT * FROM planos_cobertura ORDER BY id";
        List<PlanoCobertura> planos = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                planos.add(mapear(resultSet));
            }
            return planos;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar planos de cobertura.", exception);
        }
    }

    public Optional<PlanoCobertura> buscarPorId(Long id) {
        String sql = "SELECT * FROM planos_cobertura WHERE id = ?";

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
            throw new DataAccessException("Erro ao buscar plano de cobertura.", exception);
        }
    }

    public List<PlanoCobertura> listarPorCampanha(Long campanhaId) {
        String sql = "SELECT * FROM planos_cobertura WHERE campanha_id = ? ORDER BY id";
        List<PlanoCobertura> planos = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    planos.add(mapear(resultSet));
                }
            }

            return planos;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar planos da campanha.", exception);
        }
    }

    public boolean nomeExisteParaCampanha(String nome, Long campanhaId, Long idIgnorado) {
        String sql = "SELECT COUNT(*) total FROM planos_cobertura WHERE LOWER(nome) = LOWER(?) AND campanha_id = ?"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setLong(2, campanhaId);
            if (idIgnorado != null) {
                statement.setLong(3, idIgnorado);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar plano duplicado.", exception);
        }
    }

    public PlanoCobertura inserir(PlanoCobertura plano) {
        String sql = """
                INSERT INTO planos_cobertura
                (campanha_id, nome, descricao, custo_total, alcance_total, viabilidade_geral)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setLong(1, plano.getCampanhaId());
            statement.setString(2, plano.getNome());
            statement.setString(3, plano.getDescricao());
            statement.setBigDecimal(4, plano.getCustoTotal());
            statement.setInt(5, plano.getAlcanceTotal());
            statement.setString(6, plano.getViabilidadeGeral());
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    plano.setId(keys.getLong(1));
                }
            }

            return plano;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir plano de cobertura.", exception);
        }
    }

    public boolean atualizar(Long id, PlanoCobertura plano) {
        String sql = """
                UPDATE planos_cobertura
                   SET campanha_id = ?, nome = ?, descricao = ?, custo_total = ?,
                       alcance_total = ?, viabilidade_geral = ?
                 WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, plano.getCampanhaId());
            statement.setString(2, plano.getNome());
            statement.setString(3, plano.getDescricao());
            statement.setBigDecimal(4, plano.getCustoTotal());
            statement.setInt(5, plano.getAlcanceTotal());
            statement.setString(6, plano.getViabilidadeGeral());
            statement.setLong(7, id);
            boolean atualizado = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return atualizado;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao atualizar plano de cobertura.", exception);
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM planos_cobertura WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover plano de cobertura.", exception);
        }
    }

    private PlanoCobertura mapear(ResultSet resultSet) throws Exception {
        return new PlanoCobertura(
                resultSet.getLong("id"),
                resultSet.getLong("campanha_id"),
                resultSet.getString("nome"),
                resultSet.getString("descricao"),
                resultSet.getBigDecimal("custo_total"),
                resultSet.getInt("alcance_total"),
                resultSet.getString("viabilidade_geral")
        );
    }
}
