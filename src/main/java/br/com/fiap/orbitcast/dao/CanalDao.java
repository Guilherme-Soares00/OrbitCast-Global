package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.Canal;
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
public class CanalDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<Canal> listar() {
        String sql = "SELECT * FROM canais ORDER BY id";
        List<Canal> canais = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                canais.add(mapear(resultSet));
            }
            return canais;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar canais.", exception);
        }
    }

    public Optional<Canal> buscarPorId(Long id) {
        String sql = "SELECT * FROM canais WHERE id = ?";

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
            throw new DataAccessException("Erro ao buscar canal.", exception);
        }
    }

    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) total FROM canais WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar canal.", exception);
        }
    }

    public boolean nomeExisteParaCliente(String nome, Long clienteId, Long idIgnorado) {
        String sql = "SELECT COUNT(*) total FROM canais WHERE LOWER(nome) = LOWER(?) AND cliente_id = ?"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setLong(2, clienteId);
            if (idIgnorado != null) {
                statement.setLong(3, idIgnorado);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar nome do canal.", exception);
        }
    }

    public Canal inserir(Canal canal) {
        String sql = """
                INSERT INTO canais (cliente_id, nome, tipo_conteudo, publico_alvo, classificacao_indicativa, ativo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            canal.setAtivo(canal.getAtivo() == null ? Boolean.TRUE : canal.getAtivo());

            statement.setLong(1, canal.getClienteId());
            statement.setString(2, canal.getNome());
            statement.setString(3, canal.getTipoConteudo());
            statement.setString(4, canal.getPublicoAlvo());
            statement.setString(5, canal.getClassificacaoIndicativa());
            statement.setBoolean(6, canal.getAtivo());
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    canal.setId(keys.getLong(1));
                }
            }

            return canal;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir canal.", exception);
        }
    }

    public boolean atualizar(Long id, Canal canal) {
        String sql = """
                UPDATE canais
                   SET cliente_id = ?, nome = ?, tipo_conteudo = ?, publico_alvo = ?,
                       classificacao_indicativa = ?, ativo = ?
                 WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, canal.getClienteId());
            statement.setString(2, canal.getNome());
            statement.setString(3, canal.getTipoConteudo());
            statement.setString(4, canal.getPublicoAlvo());
            statement.setString(5, canal.getClassificacaoIndicativa());
            statement.setBoolean(6, canal.getAtivo() == null || canal.getAtivo());
            statement.setLong(7, id);
            boolean atualizado = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return atualizado;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao atualizar canal.", exception);
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM canais WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover canal.", exception);
        }
    }

    private Canal mapear(ResultSet resultSet) throws Exception {
        return new Canal(
                resultSet.getLong("id"),
                resultSet.getLong("cliente_id"),
                resultSet.getString("nome"),
                resultSet.getString("tipo_conteudo"),
                resultSet.getString("publico_alvo"),
                resultSet.getString("classificacao_indicativa"),
                resultSet.getBoolean("ativo")
        );
    }
}
