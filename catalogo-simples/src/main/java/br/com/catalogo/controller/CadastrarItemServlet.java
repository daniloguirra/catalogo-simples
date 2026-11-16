package br.com.catalogo.controller;

import br.com.catalogo.service.ItemMidiaForm;
import br.com.catalogo.service.ValidacaoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CadastrarItemServlet", urlPatterns = "/itens/novo")
public class CadastrarItemServlet extends ItemMidiaServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        mostrarFormulario(req, resp, new ItemMidiaForm(), List.of(), null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ItemMidiaForm form = lerFormulario(req);
        try {
            service.cadastrar(form);
            redirecionarParaLista(req, resp, SUCESSO_CADASTRADO);
        } catch (ValidacaoException e) {
            mostrarFormulario(req, resp, form, e.getErros(), null);
        }
    }
}
