package br.com.catalogo.controller;

import br.com.catalogo.model.ItemMidia;
import br.com.catalogo.service.ItemMidiaForm;
import br.com.catalogo.service.ValidacaoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "EditarItemServlet", urlPatterns = "/itens/editar")
public class EditarItemServlet extends ItemMidiaServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = lerId(req);
        Optional<ItemMidia> item = (id == null) ? Optional.empty() : service.buscarPorId(id);

        if (item.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Item não encontrado.");
            return;
        }
        mostrarFormulario(req, resp, ItemMidiaForm.de(item.get()), List.of(), id);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = lerId(req);
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador do item inválido.");
            return;
        }

        ItemMidiaForm form = lerFormulario(req);
        try {
            boolean atualizado = service.atualizar(id, form);
            if (atualizado) {
                redirecionarParaLista(req, resp, SUCESSO_ATUALIZADO);
            } else {
                resp.sendRedirect(req.getContextPath() + "/itens?erro=nao-encontrado");
            }
        } catch (ValidacaoException e) {
            mostrarFormulario(req, resp, form, e.getErros(), id);
        }
    }
}
