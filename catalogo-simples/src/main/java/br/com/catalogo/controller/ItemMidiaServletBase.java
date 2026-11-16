package br.com.catalogo.controller;

import br.com.catalogo.dao.ItemMidiaDAOJdbc;
import br.com.catalogo.model.TipoMidia;
import br.com.catalogo.service.ItemMidiaForm;
import br.com.catalogo.service.ItemMidiaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public abstract class ItemMidiaServletBase extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected static final String PASTA_VIEWS = "/WEB-INF/views/";

    protected static final String SUCESSO_CADASTRADO = "cadastrado";
    protected static final String SUCESSO_ATUALIZADO = "atualizado";
    protected static final String SUCESSO_EXCLUIDO = "excluido";

    protected transient ItemMidiaService service;

    @Override
    public void init() throws ServletException {
        this.service = new ItemMidiaService(new ItemMidiaDAOJdbc());
    }

    protected void renderizar(String jsp, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("tipos", TipoMidia.values());
        req.getRequestDispatcher(PASTA_VIEWS + jsp).forward(req, resp);
    }

    protected void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp,
                                     ItemMidiaForm form, List<String> erros, Integer id)
            throws ServletException, IOException {
        req.setAttribute("form", form);
        req.setAttribute("erros", erros);
        req.setAttribute("itemId", id);
        req.setAttribute("modoEdicao", id != null);
        renderizar("formularioItem.jsp", req, resp);
    }

    protected ItemMidiaForm lerFormulario(HttpServletRequest req) {
        ItemMidiaForm form = new ItemMidiaForm();
        form.setTitulo(req.getParameter("titulo"));
        form.setAutorDiretor(req.getParameter("autorDiretor"));
        form.setAnoLancamento(req.getParameter("anoLancamento"));
        form.setGenero(req.getParameter("genero"));
        form.setSinopse(req.getParameter("sinopse"));
        form.setTipoMidia(req.getParameter("tipoMidia"));
        return form;
    }

    protected Integer lerId(HttpServletRequest req) {
        String valor = req.getParameter("id");
        if (valor == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(valor.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void redirecionarParaLista(HttpServletRequest req, HttpServletResponse resp, String sucesso)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/itens?sucesso=" + sucesso);
    }
}
