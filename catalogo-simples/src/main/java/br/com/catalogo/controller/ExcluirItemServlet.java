package br.com.catalogo.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExcluirItemServlet", urlPatterns = "/itens/excluir")
public class ExcluirItemServlet extends ItemMidiaServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = lerId(req);
        if (id != null && service.excluir(id)) {
            redirecionarParaLista(req, resp, SUCESSO_EXCLUIDO);
        } else {
            resp.sendRedirect(req.getContextPath() + "/itens?erro=nao-encontrado");
        }
    }
}
