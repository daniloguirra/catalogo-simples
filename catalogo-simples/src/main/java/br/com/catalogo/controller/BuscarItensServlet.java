package br.com.catalogo.controller;

import br.com.catalogo.model.TipoMidia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BuscarItensServlet", urlPatterns = "/itens/buscar")
public class BuscarItensServlet extends ItemMidiaServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String termo = req.getParameter("q");
        TipoMidia tipo = TipoMidia.deTexto(req.getParameter("tipo"));

        req.setAttribute("itens", service.buscar(termo, tipo));
        req.setAttribute("modoBusca", true);
        renderizar("listaItens.jsp", req, resp);
    }
}
