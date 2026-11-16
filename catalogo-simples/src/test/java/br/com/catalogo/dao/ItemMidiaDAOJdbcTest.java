package br.com.catalogo.dao;

import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "CATALOGO_DB_URL", matches = ".+")
class ItemMidiaDAOJdbcTest {

    private final ItemMidiaDAO dao = new ItemMidiaDAOJdbc();
    private final List<Integer> idsCriados = new ArrayList<>();

    @AfterEach
    void limpar() {
        idsCriados.forEach(dao::excluir);
    }

    private ItemMidia inserir(String titulo, TipoMidia tipo) {
        ItemMidia item = new ItemMidia(null, titulo, "Autor de Teste JUnit", 2020, null, null, tipo);
        dao.inserir(item);
        idsCriados.add(item.getId());
        return item;
    }

    @Test
    void cicloCompletoDoCrud() {
        ItemMidia item = inserir("Teste JUnit CRUD", TipoMidia.LIVRO);
        assertNotNull(item.getId());

        ItemMidia lido = dao.buscarPorId(item.getId()).orElseThrow();
        assertEquals("Teste JUnit CRUD", lido.getTitulo());
        assertEquals(TipoMidia.LIVRO, lido.getTipoMidia());

        lido.setTitulo("Teste JUnit CRUD (editado)");
        lido.setGenero("Teste");
        assertTrue(dao.atualizar(lido));
        assertEquals("Teste", dao.buscarPorId(item.getId()).orElseThrow().getGenero());

        assertTrue(dao.excluir(item.getId()));
        assertTrue(dao.buscarPorId(item.getId()).isEmpty());
        assertFalse(dao.excluir(item.getId()));
    }

    @Test
    void buscaPorTermoETipo() {
        inserir("Teste JUnit Busca Alfa", TipoMidia.FILME);
        inserir("Teste JUnit Busca Beta", TipoMidia.SERIE);

        assertEquals(2, dao.buscar("Teste JUnit Busca", null).size());
        assertEquals(1, dao.buscar("Teste JUnit Busca", TipoMidia.SERIE).size());
    }

    @Test
    void sqlInjectionEhTratadaComoTextoComum() {
        String malicioso = "Teste JUnit'); DROP TABLE item_midia; --";
        ItemMidia item = inserir(malicioso, TipoMidia.FILME);

        assertEquals(malicioso, dao.buscarPorId(item.getId()).orElseThrow().getTitulo());
        assertNotNull(dao.listarTodos());

        assertTrue(dao.buscar("' OR '1'='1", null).isEmpty());
    }

    @Test
    void curingasDoLikeSaoProcuradosLiteralmente() {
        inserir("Teste JUnit 100% Real", TipoMidia.LIVRO);
        inserir("Teste JUnit 1000 Real", TipoMidia.LIVRO);

        List<ItemMidia> resultado = dao.buscar("100%", null);
        assertEquals(1, resultado.size());
        assertEquals("Teste JUnit 100% Real", resultado.get(0).getTitulo());
    }
}
