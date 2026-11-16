package br.com.catalogo.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ListarItensServlet", urlPatterns = "/itens")
public class ListarItensServlet extends ItemMidiaServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("itens", service.listarTodos());
        req.setAttribute("modoBusca", false);
        renderizar("listaItens.jsp", req, resp);
    }
}
