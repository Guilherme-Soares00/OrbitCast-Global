package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.Regiao;
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
public class RegiaoDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<Regiao> listar() {
        String sql = "SELECT * FROM regioes ORDER BY id";
        List<Regiao> regioes = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                regioes.add(mapear(resultSet));
            }
            return regioes;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar regioes.", exception);
        }
    }

    public Optional<Regiao> buscarPorId(Long id) {
        String sql = "SELECT * FROM regioes WHERE id = ?";

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
            throw new DataAccessException("Erro ao buscar regiao.", exception);
        }
    }

    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) total FROM regioes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar regiao.", exception);
        }
    }

    public boolean nomeEstadoPaisExiste(String nome, String estado, String pais, Long idIgnorado) {
        String sql = """
                SELECT COUNT(*) total
                  FROM regioes
                 WHERE LOWER(nome) = LOWER(?)
                   AND estado = ?
                   AND LOWER(pais) = LOWER(?)
                """
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, estado);
            statement.setString(3, pais);
            if (idIgnorado != null) {
                statement.setLong(4, idIgnorado);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar regiao duplicada.", exception);
        }
    }

    public boolean estaAssociadaCampanha(Long id) {
        String sql = "SELECT COUNT(*) total FROM campanha_regiao WHERE regiao_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar campanhas da regiao.", exception);
        }
    }

    public Regiao inserir(Regiao regiao) {
        String sql = """
                INSERT INTO regioes
                (nome, estado, pais, populacao_estimada, indice_conectividade,
                 latitude, longitude, area_km2, prioridade_social)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, regiao.getNome());
            statement.setString(2, regiao.getEstado());
            statement.setString(3, regiao.getPais());
            statement.setObject(4, regiao.getPopulacaoEstimada());
            statement.setBigDecimal(5, regiao.getIndiceConectividade());
            statement.setBigDecimal(6, regiao.getLatitude());
            statement.setBigDecimal(7, regiao.getLongitude());
            statement.setBigDecimal(8, regiao.getAreaKm2());
            statement.setObject(9, regiao.getPrioridadeSocial());
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    regiao.setId(keys.getLong(1));
                }
            }

            return regiao;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir regiao.", exception);
        }
    }

    public boolean atualizar(Long id, Regiao regiao) {
        String sql = """
                UPDATE regioes
                   SET nome = ?, estado = ?, pais = ?, populacao_estimada = ?,
                       indice_conectividade = ?, latitude = ?, longitude = ?,
                       area_km2 = ?, prioridade_social = ?
                 WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, regiao.getNome());
            statement.setString(2, regiao.getEstado());
            statement.setString(3, regiao.getPais());
            statement.setObject(4, regiao.getPopulacaoEstimada());
            statement.setBigDecimal(5, regiao.getIndiceConectividade());
            statement.setBigDecimal(6, regiao.getLatitude());
            statement.setBigDecimal(7, regiao.getLongitude());
            statement.setBigDecimal(8, regiao.getAreaKm2());
            statement.setObject(9, regiao.getPrioridadeSocial());
            statement.setLong(10, id);
            boolean atualizado = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return atualizado;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao atualizar regiao.", exception);
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM regioes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover regiao.", exception);
        }
    }

    private Regiao mapear(ResultSet resultSet) throws Exception {
        return new Regiao(
                resultSet.getLong("id"),
                resultSet.getString("nome"),
                resultSet.getString("estado"),
                resultSet.getString("pais"),
                resultSet.getInt("populacao_estimada"),
                resultSet.getBigDecimal("indice_conectividade"),
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude"),
                resultSet.getBigDecimal("area_km2"),
                resultSet.getInt("prioridade_social")
        );
    }
}
