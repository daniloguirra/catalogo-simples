<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="${modoBusca ? 'Resultado da busca' : 'Itens do catálogo'}"/>
<%@ include file="/WEB-INF/views/fragmentos/cabecalho.jspf" %>

<c:choose>
    <c:when test="${param.sucesso eq 'cadastrado'}">
        <p class="alerta alerta-sucesso" role="status">Item cadastrado com sucesso.</p>
    </c:when>
    <c:when test="${param.sucesso eq 'atualizado'}">
        <p class="alerta alerta-sucesso" role="status">Item atualizado com sucesso.</p>
    </c:when>
    <c:when test="${param.sucesso eq 'excluido'}">
        <p class="alerta alerta-sucesso" role="status">Item excluído com sucesso.</p>
    </c:when>
    <c:when test="${param.erro eq 'nao-encontrado'}">
        <p class="alerta alerta-erro" role="alert">O item não foi encontrado. Ele pode já ter sido excluído.</p>
    </c:when>
</c:choose>

<div class="cabecalho-pagina">
    <h1><c:out value="${tituloPagina}"/></h1>
    <a class="botao" href="<c:url value='/itens/novo'/>">+ Novo item</a>
</div>

<form class="painel busca" method="get" action="<c:url value='/itens/buscar'/>" role="search">
    <div class="campo campo-largo">
        <label for="q">Título ou autor/diretor</label>
        <input type="search" id="q" name="q" maxlength="150"
               placeholder="Ex.: Duna, Machado de Assis…" value="<c:out value='${param.q}'/>">
    </div>
    <div class="campo">
        <label for="filtroTipo">Tipo</label>
        <select id="filtroTipo" name="tipo">
            <option value="">Todos</option>
            <c:forEach var="t" items="${tipos}">
                <option value="${t.name()}" ${t.name() eq param.tipo ? 'selected' : ''}>
                    <c:out value="${t.descricao}"/>
                </option>
            </c:forEach>
        </select>
    </div>
    <div class="acoes-busca">
        <button type="submit" class="botao">Buscar</button>
        <c:if test="${modoBusca}">
            <a class="botao botao-secundario" href="<c:url value='/itens'/>">Limpar</a>
        </c:if>
    </div>
</form>

<p class="contagem">
    <c:choose>
        <c:when test="${itens.size() == 1}">1 item</c:when>
        <c:otherwise>${itens.size()} itens</c:otherwise>
    </c:choose>
    <c:if test="${modoBusca and not empty param.q}">
        para “<c:out value="${param.q}"/>”
    </c:if>
</p>

<c:choose>
    <c:when test="${empty itens}">
        <div class="painel vazio">
            <c:choose>
                <c:when test="${modoBusca}">
                    <p>Nenhum item corresponde à busca. Tente outro termo ou remova o filtro de tipo.</p>
                </c:when>
                <c:otherwise>
                    <p>O catálogo ainda está vazio.</p>
                    <a class="botao" href="<c:url value='/itens/novo'/>">Cadastrar o primeiro item</a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:otherwise>
        <div class="tabela-container">
            <table class="tabela">
                <thead>
                <tr>
                    <th scope="col">Título</th>
                    <th scope="col">Autor/Diretor</th>
                    <th scope="col" class="num">Ano</th>
                    <th scope="col">Gênero</th>
                    <th scope="col">Tipo</th>
                    <th scope="col"><span class="sr-only">Ações</span></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${itens}">
                    <tr>
                        <td data-rotulo="Título">
                            <span class="titulo-item"><c:out value="${item.titulo}"/></span>
                            <c:if test="${not empty item.sinopse}">
                                <details class="sinopse">
                                    <summary>Sinopse</summary>
                                    <p><c:out value="${item.sinopse}"/></p>
                                </details>
                            </c:if>
                        </td>
                        <td data-rotulo="Autor/Diretor"><c:out value="${item.autorDiretor}"/></td>
                        <td data-rotulo="Ano" class="num"><c:out value="${item.anoLancamento}"/></td>
                        <td data-rotulo="Gênero"><c:out value="${empty item.genero ? '—' : item.genero}"/></td>
                        <td data-rotulo="Tipo">
                            <span class="etiqueta etiqueta-${item.tipoMidia.name().toLowerCase()}">
                                <c:out value="${item.tipoMidia.descricao}"/>
                            </span>
                        </td>
                        <td class="acoes">
                            <c:url var="urlEditar" value="/itens/editar">
                                <c:param name="id" value="${item.id}"/>
                            </c:url>
                            <a class="botao botao-pequeno botao-secundario" href="${urlEditar}">Editar</a>
                            <form method="post" action="<c:url value='/itens/excluir'/>"
                                  onsubmit="return confirm('Tem certeza de que deseja excluir este item?');">
                                <input type="hidden" name="id" value="${item.id}">
                                <button type="submit" class="botao botao-pequeno botao-perigo">Excluir</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
