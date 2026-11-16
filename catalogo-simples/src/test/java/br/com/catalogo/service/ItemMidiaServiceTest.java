package br.com.catalogo.service;

import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMidiaServiceTest {

    private FakeItemMidiaDAO dao;
    private ItemMidiaService service;

    @BeforeEach
    void preparar() {
        dao = new FakeItemMidiaDAO();
        service = new ItemMidiaService(dao);
    }

    private static ItemMidiaForm formValido() {
        ItemMidiaForm form = new ItemMidiaForm();
        form.setTitulo("Dom Casmurro");
        form.setAutorDiretor("Machado de Assis");
        form.setAnoLancamento("1899");
        form.setGenero("Romance");
        form.setSinopse("Bentinho e Capitu.");
        form.setTipoMidia("LIVRO");
        return form;
    }

    private static boolean contemErro(ValidacaoException e, String trecho) {
        return e.getErros().stream().anyMatch(m -> m.contains(trecho));
    }

    @Nested
    @DisplayName("Cadastro")
    class Cadastro {

        @Test
        @DisplayName("item válido é salvo e recebe um id")
        void itemValidoEhSalvo() throws ValidacaoException {
            ItemMidia salvo = service.cadastrar(formValido());

            assertNotNull(salvo.getId());
            ItemMidia doBanco = service.buscarPorId(salvo.getId()).orElseThrow();
            assertEquals("Dom Casmurro", doBanco.getTitulo());
            assertEquals(1899, doBanco.getAnoLancamento());
            assertEquals(TipoMidia.LIVRO, doBanco.getTipoMidia());
        }

        @Test
        @DisplayName("espaços nas pontas são removidos e campos opcionais vazios viram null")
        void normalizaCampos() throws ValidacaoException {
            ItemMidiaForm form = formValido();
            form.setTitulo("   Dom Casmurro   ");
            form.setAnoLancamento(" 1899 ");
            form.setTipoMidia("livro");
            form.setGenero("   ");
            form.setSinopse("");

            ItemMidia salvo = service.cadastrar(form);

            assertEquals("Dom Casmurro", salvo.getTitulo());
            assertEquals(1899, salvo.getAnoLancamento());
            assertEquals(TipoMidia.LIVRO, salvo.getTipoMidia());
            assertNull(salvo.getGenero());
            assertNull(salvo.getSinopse());
        }

        @Test
        @DisplayName("formulário vazio retorna todos os erros obrigatórios de uma vez e não grava nada")
        void formularioVazio() {
            ValidacaoException e = assertThrows(ValidacaoException.class,
                    () -> service.cadastrar(new ItemMidiaForm()));

            assertTrue(contemErro(e, "título é obrigatório"));
            assertTrue(contemErro(e, "autor/diretor é obrigatório"));
            assertTrue(contemErro(e, "ano de lançamento é obrigatório"));
            assertTrue(contemErro(e, "tipo de mídia"));
            assertEquals(4, e.getErros().size());
            assertTrue(service.listarTodos().isEmpty());
        }

        @Test
        @DisplayName("título só com espaços é tratado como vazio")
        void tituloEmBranco() {
            ItemMidiaForm form = formValido();
            form.setTitulo("     ");

            ValidacaoException e = assertThrows(ValidacaoException.class, () -> service.cadastrar(form));
            assertTrue(contemErro(e, "título é obrigatório"));
        }

        @ParameterizedTest(name = "ano \"{0}\" não é número")
        @ValueSource(strings = {"abc", "19x9", "1999.5", "2 000"})
        void anoNaoNumerico(String ano) {
            ItemMidiaForm form = formValido();
            form.setAnoLancamento(ano);

            ValidacaoException e = assertThrows(ValidacaoException.class, () -> service.cadastrar(form));
            assertTrue(contemErro(e, "número inteiro"));
        }

        @ParameterizedTest(name = "ano {0} fora da faixa")
        @ValueSource(strings = {"999", "0", "-5", "3000"})
        void anoForaDaFaixa(String ano) {
            ItemMidiaForm form = formValido();
            form.setAnoLancamento(ano);

            ValidacaoException e = assertThrows(ValidacaoException.class, () -> service.cadastrar(form));
            assertTrue(contemErro(e, "deve estar entre"));
        }

        @Test
        @DisplayName("o ano máximo aceito (ano atual + 5) passa na validação")
        void anoNoLimiteSuperior() throws ValidacaoException {
            ItemMidiaForm form = formValido();
            form.setAnoLancamento(String.valueOf(ItemMidiaService.anoMaximo()));

            assertNotNull(service.cadastrar(form).getId());
        }

        @Test
        @DisplayName("textos acima do tamanho máximo são rejeitados")
        void textosLongos() {
            ItemMidiaForm form = formValido();
            form.setTitulo("a".repeat(ItemMidiaService.TAM_MAX_TITULO + 1));
            form.setAutorDiretor("b".repeat(ItemMidiaService.TAM_MAX_AUTOR + 1));
            form.setGenero("c".repeat(ItemMidiaService.TAM_MAX_GENERO + 1));
            form.setSinopse("d".repeat(ItemMidiaService.TAM_MAX_SINOPSE + 1));

            ValidacaoException e = assertThrows(ValidacaoException.class, () -> service.cadastrar(form));
            assertTrue(contemErro(e, "título deve ter no máximo 150"));
            assertTrue(contemErro(e, "autor/diretor deve ter no máximo 100"));
            assertTrue(contemErro(e, "gênero deve ter no máximo 50"));
            assertTrue(contemErro(e, "sinopse deve ter no máximo 1000"));
        }

        @Test
        @DisplayName("textos exatamente no tamanho máximo são aceitos")
        void textosNoLimite() throws ValidacaoException {
            ItemMidiaForm form = formValido();
            form.setTitulo("a".repeat(ItemMidiaService.TAM_MAX_TITULO));
            form.setSinopse("d".repeat(ItemMidiaService.TAM_MAX_SINOPSE));

            assertNotNull(service.cadastrar(form).getId());
        }

        @Test
        @DisplayName("tipo de mídia inexistente é rejeitado")
        void tipoInvalido() {
            ItemMidiaForm form = formValido();
            form.setTipoMidia("REVISTA");

            ValidacaoException e = assertThrows(ValidacaoException.class, () -> service.cadastrar(form));
            assertTrue(contemErro(e, "tipo de mídia"));
        }

        @Test
        @DisplayName("HTML digitado é guardado como texto comum (o escape acontece na JSP)")
        void htmlEhGuardadoComoTexto() throws ValidacaoException {
            ItemMidiaForm form = formValido();
            form.setTitulo("<script>alert('x')</script>");

            ItemMidia salvo = service.cadastrar(form);
            assertEquals("<script>alert('x')</script>", salvo.getTitulo());
        }
    }

    @Nested
    @DisplayName("Busca")
    class Busca {

        @BeforeEach
        void popular() throws ValidacaoException {
            cadastrar("Duna", "Frank Herbert", "1965", "LIVRO");
            cadastrar("Duna", "Denis Villeneuve", "2021", "FILME");
            cadastrar("Breaking Bad", "Vince Gilligan", "2008", "SERIE");
        }

        private void cadastrar(String titulo, String autor, String ano, String tipo) throws ValidacaoException {
            ItemMidiaForm form = new ItemMidiaForm();
            form.setTitulo(titulo);
            form.setAutorDiretor(autor);
            form.setAnoLancamento(ano);
            form.setTipoMidia(tipo);
            service.cadastrar(form);
        }

        @Test
        @DisplayName("encontra pelo título, sem diferenciar maiúsculas")
        void porTitulo() {
            assertEquals(2, service.buscar("duna", null).size());
        }

        @Test
        @DisplayName("encontra pelo autor/diretor")
        void porAutor() {
            List<ItemMidia> resultado = service.buscar("gilligan", null);
            assertEquals(1, resultado.size());
            assertEquals("Breaking Bad", resultado.get(0).getTitulo());
        }

        @Test
        @DisplayName("combina termo e filtro de tipo")
        void termoETipo() {
            List<ItemMidia> resultado = service.buscar("Duna", TipoMidia.FILME);
            assertEquals(1, resultado.size());
            assertEquals("Denis Villeneuve", resultado.get(0).getAutorDiretor());
        }

        @Test
        @DisplayName("termo vazio ou só com espaços traz todos os itens")
        void termoVazio() {
            assertEquals(3, service.buscar("   ", null).size());
            assertEquals(3, service.buscar(null, null).size());
        }

        @Test
        @DisplayName("filtra só pelo tipo")
        void apenasTipo() {
            assertEquals(1, service.buscar(null, TipoMidia.SERIE).size());
        }

        @Test
        @DisplayName("termo sem correspondência retorna lista vazia")
        void semResultado() {
            assertTrue(service.buscar("Senhor dos Anéis", null).isEmpty());
        }
    }

    @Nested
    @DisplayName("Edição e exclusão")
    class EdicaoExclusao {

        @Test
        @DisplayName("atualiza um item existente")
        void atualizaExistente() throws ValidacaoException {
            int id = service.cadastrar(formValido()).getId();
            ItemMidiaForm alterado = formValido();
            alterado.setTitulo("Memórias Póstumas de Brás Cubas");
            alterado.setAnoLancamento("1881");

            assertTrue(service.atualizar(id, alterado));

            ItemMidia doBanco = service.buscarPorId(id).orElseThrow();
            assertEquals("Memórias Póstumas de Brás Cubas", doBanco.getTitulo());
            assertEquals(1881, doBanco.getAnoLancamento());
        }

        @Test
        @DisplayName("atualizar item inexistente retorna false")
        void atualizaInexistente() throws ValidacaoException {
            assertFalse(service.atualizar(999, formValido()));
        }

        @Test
        @DisplayName("dados inválidos na edição não alteram o item salvo")
        void edicaoInvalidaNaoAltera() throws ValidacaoException {
            int id = service.cadastrar(formValido()).getId();
            ItemMidiaForm invalido = formValido();
            invalido.setTitulo("");

            assertThrows(ValidacaoException.class, () -> service.atualizar(id, invalido));
            assertEquals("Dom Casmurro", service.buscarPorId(id).orElseThrow().getTitulo());
        }

        @Test
        @DisplayName("exclui um item e uma segunda exclusão retorna false")
        void exclui() throws ValidacaoException {
            int id = service.cadastrar(formValido()).getId();

            assertTrue(service.excluir(id));
            assertTrue(service.buscarPorId(id).isEmpty());
            assertFalse(service.excluir(id));
        }
    }
}
