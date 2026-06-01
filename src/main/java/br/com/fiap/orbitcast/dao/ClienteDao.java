package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.Cliente;
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
public class ClienteDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<Cliente> listar() {
        String sql = "SELECT * FROM clientes ORDER BY id";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clientes.add(mapear(resultSet));
            }
            return clientes;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar clientes.", exception);
        }
    }

    public Optional<Cliente> buscarPorId(Long id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

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
            throw new DataAccessException("Erro ao buscar cliente.", exception);
        }
    }

    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) total FROM clientes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar cliente.", exception);
        }
    }

    public boolean documentoExiste(String documento, Long idIgnorado) {
        String sql = "SELECT COUNT(*) total FROM clientes WHERE documento = ?"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, documento);
            if (idIgnorado != null) {
                statement.setLong(2, idIgnorado);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar documento do cliente.", exception);
        }
    }

    public boolean emailExiste(String email, Long idIgnorado) {
        String sql = "SELECT COUNT(*) total FROM clientes WHERE LOWER(email) = LOWER(?)"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            if (idIgnorado != null) {
                statement.setLong(2, idIgnorado);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar email do cliente.", exception);
        }
    }

    public boolean possuiCanais(Long id) {
        String sql = "SELECT COUNT(*) total FROM canais WHERE cliente_id = ?";
        return existeVinculo(sql, id, "Erro ao verificar canais do cliente.");
    }

    public boolean possuiCampanhas(Long id) {
        String sql = "SELECT COUNT(*) total FROM campanhas_transmissao WHERE cliente_id = ?";
        return existeVinculo(sql, id, "Erro ao verificar campanhas do cliente.");
    }

    public Cliente inserir(Cliente cliente) {
        String sql = """
                INSERT INTO clientes (nome, documento, email, telefone, segmento, data_cadastro, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            cliente.setDataCadastro(cliente.getDataCadastro() == null ? LocalDateTime.now() : cliente.getDataCadastro());
            cliente.setAtivo(cliente.getAtivo() == null ? Boolean.TRUE : cliente.getAtivo());

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getDocumento());
            statement.setString(3, cliente.getEmail());
            statement.setString(4, cliente.getTelefone());
            statement.setString(5, cliente.getSegmento());
            statement.setTimestamp(6, Timestamp.valueOf(cliente.getDataCadastro()));
            statement.setBoolean(7, cliente.getAtivo());
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    cliente.setId(keys.getLong(1));
                }
            }

            return cliente;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir cliente.", exception);
        }
    }

    public boolean atualizar(Long id, Cliente cliente) {
        String sql = """
                UPDATE clientes
                   SET nome = ?, documento = ?, email = ?, telefone = ?, segmento = ?, ativo = ?
                 WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getDocumento());
            statement.setString(3, cliente.getEmail());
            statement.setString(4, cliente.getTelefone());
            statement.setString(5, cliente.getSegmento());
            statement.setBoolean(6, cliente.getAtivo() == null || cliente.getAtivo());
            statement.setLong(7, id);
            boolean atualizado = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return atualizado;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao atualizar cliente.", exception);
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover cliente.", exception);
        }
    }

    private Cliente mapear(ResultSet resultSet) throws Exception {
        return new Cliente(
                resultSet.getLong("id"),
                resultSet.getString("nome"),
                resultSet.getString("documento"),
                resultSet.getString("email"),
                resultSet.getString("telefone"),
                resultSet.getString("segmento"),
                resultSet.getTimestamp("data_cadastro").toLocalDateTime(),
                resultSet.getBoolean("ativo")
        );
    }

    private boolean existeVinculo(String sql, Long id, String mensagemErro) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException(mensagemErro, exception);
        }
    }
}
