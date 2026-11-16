<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="tituloPagina" value="Erro"/>
<%@ include file="/WEB-INF/views/fragmentos/cabecalho.jspf" %>

<div class="painel pagina-erro">
    <c:choose>
        <c:when test="${codigo == 404}">
            <h1>Página não encontrada</h1>
            <p>O endereço acessado não existe ou o item foi excluído.</p>
        </c:when>
        <c:when test="${codigo == 400}">
            <h1>Requisição inválida</h1>
            <p>Os dados enviados estão incompletos ou em formato inválido.</p>
        </c:when>
        <c:when test="${codigo == 405}">
            <h1>Operação não permitida</h1>
            <p>Essa ação só pode ser feita pelos botões da aplicação.</p>
        </c:when>
        <c:otherwise>
            <h1>Algo deu errado</h1>
            <p>Não foi possível concluir a operação. Verifique se o banco de dados MySQL
               está em execução e tente novamente em instantes.</p>
        </c:otherwise>
    </c:choose>
    <a class="botao" href="<c:url value='/itens'/>">Voltar ao catálogo</a>
</div>

<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
