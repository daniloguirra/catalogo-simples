package br.com.catalogo.service;

import br.com.catalogo.dao.ItemMidiaDAO;
import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class FakeItemMidiaDAO implements ItemMidiaDAO {

    private final Map<Integer, ItemMidia> itens = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public void inserir(ItemMidia item) {
        item.setId(proximoId++);
        itens.put(item.getId(), copiar(item));
    }

    @Override
    public boolean atualizar(ItemMidia item) {
        if (!itens.containsKey(item.getId())) {
            return false;
        }
        itens.put(item.getId(), copiar(item));
        return true;
    }

    @Override
    public boolean excluir(int id) {
        return itens.remove(id) != null;
    }

    @Override
    public Optional<ItemMidia> buscarPorId(int id) {
        return Optional.ofNullable(itens.get(id)).map(this::copiar);
    }

    @Override
    public List<ItemMidia> listarTodos() {
        return buscar(null, null);
    }

    @Override
    public List<ItemMidia> buscar(String termo, TipoMidia tipo) {
        String termoMinusculo = termo == null ? null : termo.toLowerCase(Locale.ROOT);
        return itens.values().stream()
                .filter(i -> tipo == null || i.getTipoMidia() == tipo)
                .filter(i -> termoMinusculo == null
                        || i.getTitulo().toLowerCase(Locale.ROOT).contains(termoMinusculo)
                        || i.getAutorDiretor().toLowerCase(Locale.ROOT).contains(termoMinusculo))
                .sorted(Comparator.comparing(ItemMidia::getTitulo))
                .map(this::copiar)
                .collect(Collectors.toList());
    }

    private ItemMidia copiar(ItemMidia i) {
        return new ItemMidia(i.getId(), i.getTitulo(), i.getAutorDiretor(), i.getAnoLancamento(),
                i.getGenero(), i.getSinopse(), i.getTipoMidia());
    }
}
