package br.com.catalogo.dao;

import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.model.TipoMidia;

import java.util.List;
import java.util.Optional;

public interface ItemMidiaDAO {

    void inserir(ItemMidia item);

    boolean atualizar(ItemMidia item);

    boolean excluir(int id);

    Optional<ItemMidia> buscarPorId(int id);

    List<ItemMidia> listarTodos();

    List<ItemMidia> buscar(String termo, TipoMidia tipo);
}
