package br.com.catalogo.service;

import br.com.catalogo.dao.ItemMidiaDAO;
import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemMidiaService {

    public static final int TAM_MAX_TITULO = 150;
    public static final int TAM_MAX_AUTOR = 100;
    public static final int TAM_MAX_GENERO = 50;
    public static final int TAM_MAX_SINOPSE = 1000;
    public static final int ANO_MINIMO = 1000;
    public static final int ANOS_FUTUROS_ACEITOS = 5;

    private final ItemMidiaDAO dao;

    public ItemMidiaService(ItemMidiaDAO dao) {
        this.dao = Objects.requireNonNull(dao, "dao");
    }

    public List<ItemMidia> listarTodos() {
        return dao.listarTodos();
    }

    public Optional<ItemMidia> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<ItemMidia> buscar(String termo, TipoMidia tipo) {
        String termoLimpo = limpar(termo);
        if (termoLimpo != null && termoLimpo.length() > TAM_MAX_TITULO) {
            termoLimpo = termoLimpo.substring(0, TAM_MAX_TITULO);
        }
        return dao.buscar(termoLimpo, tipo);
    }

    public ItemMidia cadastrar(ItemMidiaForm form) throws ValidacaoException {
        ItemMidia item = converterEValidar(form);
        dao.inserir(item);
        return item;
    }

    public boolean atualizar(int id, ItemMidiaForm form) throws ValidacaoException {
        ItemMidia item = converterEValidar(form);
        item.setId(id);
        return dao.atualizar(item);
    }

    public boolean excluir(int id) {
        return dao.excluir(id);
    }

    public static int anoMaximo() {
        return Year.now().getValue() + ANOS_FUTUROS_ACEITOS;
    }

    private ItemMidia converterEValidar(ItemMidiaForm form) throws ValidacaoException {
        List<String> erros = new ArrayList<>();

        String titulo = limpar(form.getTitulo());
        validarTexto(titulo, "O título", TAM_MAX_TITULO, true, erros);

        String autorDiretor = limpar(form.getAutorDiretor());
        validarTexto(autorDiretor, "O autor/diretor", TAM_MAX_AUTOR, true, erros);

        Integer ano = validarAno(limpar(form.getAnoLancamento()), erros);

        String genero = limpar(form.getGenero());
        validarTexto(genero, "O gênero", TAM_MAX_GENERO, false, erros);

        String sinopse = limpar(form.getSinopse());
        validarTexto(sinopse, "A sinopse", TAM_MAX_SINOPSE, false, erros);

        TipoMidia tipo = TipoMidia.deTexto(form.getTipoMidia());
        if (tipo == null) {
            erros.add("Selecione o tipo de mídia: Livro, Série ou Filme.");
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
        return new ItemMidia(null, titulo, autorDiretor, ano, genero, sinopse, tipo);
    }

    private void validarTexto(String valor, String nomeCampo, int tamanhoMaximo,
                              boolean obrigatorio, List<String> erros) {
        if (valor == null) {
            if (obrigatorio) {
                erros.add(nomeCampo + " é obrigatório.");
            }
            return;
        }
        if (valor.length() > tamanhoMaximo) {
            erros.add(nomeCampo + " deve ter no máximo " + tamanhoMaximo
                    + " caracteres (atualmente tem " + valor.length() + ").");
        }
    }

    private Integer validarAno(String texto, List<String> erros) {
        if (texto == null) {
            erros.add("O ano de lançamento é obrigatório.");
            return null;
        }
        int ano;
        try {
            ano = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            erros.add("O ano de lançamento deve ser um número inteiro (ex.: 1999).");
            return null;
        }
        int maximo = anoMaximo();
        if (ano < ANO_MINIMO || ano > maximo) {
            erros.add("O ano de lançamento deve estar entre " + ANO_MINIMO + " e " + maximo + ".");
            return null;
        }
        return ano;
    }

    private static String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String semEspacos = valor.trim();
        return semEspacos.isEmpty() ? null : semEspacos;
    }
}
