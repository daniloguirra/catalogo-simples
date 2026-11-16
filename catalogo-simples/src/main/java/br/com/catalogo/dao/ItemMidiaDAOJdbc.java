package br.com.catalogo.dao;

import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemMidiaDAOJdbc implements ItemMidiaDAO {

    private static final String COLUNAS =
            "id, titulo, autor_diretor, ano_lancamento, genero, sinopse, tipo_midia";

    private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + " FROM item_midia";

    private static final String SQL_INSERIR =
            "INSERT INTO item_midia (titulo, autor_diretor, ano_lancamento, genero, sinopse, tipo_midia) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
            "UPDATE item_midia SET titulo = ?, autor_diretor = ?, ano_lancamento = ?, "
                    + "genero = ?, sinopse = ?, tipo_midia = ? WHERE id = ?";

    private static final String SQL_EXCLUIR = "DELETE FROM item_midia WHERE id = ?";

    @FunctionalInterface
    private interface PreparadorDeParametros {
        void preparar(PreparedStatement stmt) throws SQLException;
    }

    @Override
    public void inserir(ItemMidia item) {
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            preencherCampos(stmt, item);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    item.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir item no catálogo.", e);
        }
    }

    @Override
    public boolean atualizar(ItemMidia item) {
        int linhas = executarAtualizacao(SQL_ATUALIZAR, stmt -> {
            int proximo = preencherCampos(stmt, item);
            stmt.setInt(proximo, item.getId());
        });
        return linhas > 0;
    }

    @Override
    public boolean excluir(int id) {
        return executarAtualizacao(SQL_EXCLUIR, stmt -> stmt.setInt(1, id)) > 0;
    }

    @Override
    public Optional<ItemMidia> buscarPorId(int id) {
        List<ItemMidia> resultado = consultar(SQL_SELECT_BASE + " WHERE id = ?", stmt -> stmt.setInt(1, id));
        return resultado.stream().findFirst();
    }

    @Override
    public List<ItemMidia> listarTodos() {
        return consultar(SQL_SELECT_BASE + " ORDER BY titulo, ano_lancamento", stmt -> { });
    }

    @Override
    public List<ItemMidia> buscar(String termo, TipoMidia tipo) {
        boolean filtrarPorTexto = termo != null && !termo.isBlank();

        StringBuilder sql = new StringBuilder(SQL_SELECT_BASE).append(" WHERE 1 = 1");
        if (filtrarPorTexto) {
            sql.append(" AND (titulo LIKE ? OR autor_diretor LIKE ?)");
        }
        if (tipo != null) {
            sql.append(" AND tipo_midia = ?");
        }
        sql.append(" ORDER BY titulo, ano_lancamento");

        return consultar(sql.toString(), stmt -> {
            int indice = 1;
            if (filtrarPorTexto) {
                String padrao = "%" + escaparCuringasLike(termo.trim()) + "%";
                stmt.setString(indice++, padrao);
                stmt.setString(indice++, padrao);
            }
            if (tipo != null) {
                stmt.setString(indice, tipo.name());
            }
        });
    }

    private List<ItemMidia> consultar(String sql, PreparadorDeParametros preparador) {
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preparador.preparar(stmt);

            List<ItemMidia> itens = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearLinha(rs));
                }
            }
            return itens;
        } catch (SQLException e) {
            throw new DAOException("Erro ao consultar itens do catálogo.", e);
        }
    }

    private int executarAtualizacao(String sql, PreparadorDeParametros preparador) {
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preparador.preparar(stmt);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao gravar alterações no catálogo.", e);
        }
    }

    private int preencherCampos(PreparedStatement stmt, ItemMidia item) throws SQLException {
        stmt.setString(1, item.getTitulo());
        stmt.setString(2, item.getAutorDiretor());
        stmt.setInt(3, item.getAnoLancamento());
        definirTextoOpcional(stmt, 4, item.getGenero());
        definirTextoOpcional(stmt, 5, item.getSinopse());
        stmt.setString(6, item.getTipoMidia().name());
        return 7;
    }

    private void definirTextoOpcional(PreparedStatement stmt, int indice, String valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(indice, Types.VARCHAR);
        } else {
            stmt.setString(indice, valor);
        }
    }

    private ItemMidia mapearLinha(ResultSet rs) throws SQLException {
        ItemMidia item = new ItemMidia();
        item.setId(rs.getInt("id"));
        item.setTitulo(rs.getString("titulo"));
        item.setAutorDiretor(rs.getString("autor_diretor"));
        item.setAnoLancamento(rs.getInt("ano_lancamento"));
        item.setGenero(rs.getString("genero"));
        item.setSinopse(rs.getString("sinopse"));
        item.setTipoMidia(TipoMidia.valueOf(rs.getString("tipo_midia")));
        return item;
    }

    private static String escaparCuringasLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
