package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.entities.CampanhaRegiao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.exceptions.DataAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CampanhaTransmissaoDao {

    @Inject
    DatabaseConnection databaseConnection;

    public List<CampanhaTransmissao> listar() {
        String sql = "SELECT * FROM campanhas_transmissao ORDER BY id";
        List<CampanhaTransmissao> campanhas = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                campanhas.add(mapear(resultSet));
            }
            return campanhas;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar campanhas.", exception);
        }
    }

    public Optional<CampanhaTransmissao> buscarPorId(Long id) {
        String sql = "SELECT * FROM campanhas_transmissao WHERE id = ?";

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
            throw new DataAccessException("Erro ao buscar campanha.", exception);
        }
    }

    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) total FROM campanhas_transmissao WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar campanha.", exception);
        }
    }

    public boolean existePorCanal(Long canalId) {
        String sql = "SELECT COUNT(*) total FROM campanhas_transmissao WHERE canal_id = ?";
        return existeVinculo(sql, canalId, "Erro ao verificar campanhas do canal.");
    }

    public boolean possuiRegioes(Long campanhaId) {
        String sql = "SELECT COUNT(*) total FROM campanha_regiao WHERE campanha_id = ?";
        return existeVinculo(sql, campanhaId, "Erro ao verificar regioes da campanha.");
    }

    public CampanhaTransmissao inserir(CampanhaTransmissao campanha) {
        String sql = """
                INSERT INTO campanhas_transmissao
                (cliente_id, canal_id, nome, descricao, data_inicio, data_fim,
                 duracao_horas, qualidade_desejada, orcamento, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            campanha.setStatus(campanha.getStatus() == null ? "PLANEJADA" : campanha.getStatus());

            statement.setLong(1, campanha.getClienteId());
            statement.setLong(2, campanha.getCanalId());
            statement.setString(3, campanha.getNome());
            statement.setString(4, campanha.getDescricao());
            statement.setDate(5, Date.valueOf(campanha.getDataInicio()));
            statement.setDate(6, Date.valueOf(campanha.getDataFim()));
            statement.setInt(7, campanha.getDuracaoHoras());
            statement.setString(8, campanha.getQualidadeDesejada());
            statement.setBigDecimal(9, campanha.getOrcamento());
            statement.setString(10, campanha.getStatus());
            statement.executeUpdate();
            databaseConnection.commit(connection);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    campanha.setId(keys.getLong(1));
                }
            }

            return campanha;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao inserir campanha.", exception);
        }
    }

    public boolean atualizar(Long id, CampanhaTransmissao campanha) {
        String sql = """
                UPDATE campanhas_transmissao
                   SET cliente_id = ?, canal_id = ?, nome = ?, descricao = ?,
                       data_inicio = ?, data_fim = ?, duracao_horas = ?,
                       qualidade_desejada = ?, orcamento = ?, status = ?
                 WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanha.getClienteId());
            statement.setLong(2, campanha.getCanalId());
            statement.setString(3, campanha.getNome());
            statement.setString(4, campanha.getDescricao());
            statement.setDate(5, Date.valueOf(campanha.getDataInicio()));
            statement.setDate(6, Date.valueOf(campanha.getDataFim()));
            statement.setInt(7, campanha.getDuracaoHoras());
            statement.setString(8, campanha.getQualidadeDesejada());
            statement.setBigDecimal(9, campanha.getOrcamento());
            statement.setString(10, campanha.getStatus());
            statement.setLong(11, id);
            boolean atualizado = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return atualizado;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao atualizar campanha.", exception);
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM campanhas_transmissao WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover campanha.", exception);
        }
    }

    public void adicionarRegiao(CampanhaRegiao campanhaRegiao) {
        String sql = """
                INSERT INTO campanha_regiao (campanha_id, regiao_id, prioridade, observacao)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaRegiao.getCampanhaId());
            statement.setLong(2, campanhaRegiao.getRegiaoId());
            statement.setInt(3, campanhaRegiao.getPrioridade());
            statement.setString(4, campanhaRegiao.getObservacao());
            statement.executeUpdate();
            databaseConnection.commit(connection);
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao associar regiao a campanha.", exception);
        }
    }

    public boolean removerRegiao(Long campanhaId, Long regiaoId) {
        String sql = "DELETE FROM campanha_regiao WHERE campanha_id = ? AND regiao_id = ?";

        try (Connection connection = databaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaId);
            statement.setLong(2, regiaoId);
            boolean removido = statement.executeUpdate() > 0;
            databaseConnection.commit(connection);
            return removido;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao remover regiao da campanha.", exception);
        }
    }

    public boolean campanhaPossuiRegiao(Long campanhaId, Long regiaoId) {
        String sql = "SELECT COUNT(*) total FROM campanha_regiao WHERE campanha_id = ? AND regiao_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaId);
            statement.setLong(2, regiaoId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao verificar regioes da campanha.", exception);
        }
    }

    public List<Regiao> listarRegioes(Long campanhaId) {
        String sql = """
                SELECT r.*
                  FROM regioes r
                  JOIN campanha_regiao cr ON cr.regiao_id = r.id
                 WHERE cr.campanha_id = ?
                 ORDER BY cr.prioridade DESC, r.nome
                """;
        List<Regiao> regioes = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, campanhaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    regioes.add(mapearRegiao(resultSet));
                }
            }

            return regioes;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao listar regioes da campanha.", exception);
        }
    }

    private CampanhaTransmissao mapear(ResultSet resultSet) throws Exception {
        return new CampanhaTransmissao(
                resultSet.getLong("id"),
                resultSet.getLong("cliente_id"),
                resultSet.getLong("canal_id"),
                resultSet.getString("nome"),
                resultSet.getString("descricao"),
                resultSet.getDate("data_inicio").toLocalDate(),
                resultSet.getDate("data_fim").toLocalDate(),
                resultSet.getInt("duracao_horas"),
                resultSet.getString("qualidade_desejada"),
                resultSet.getBigDecimal("orcamento"),
                resultSet.getString("status")
        );
    }

    private Regiao mapearRegiao(ResultSet resultSet) throws Exception {
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
