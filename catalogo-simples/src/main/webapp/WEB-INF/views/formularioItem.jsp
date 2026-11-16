<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="${modoEdicao ? 'Editar item' : 'Cadastrar item'}"/>
<%@ include file="/WEB-INF/views/fragmentos/cabecalho.jspf" %>

<div class="cabecalho-pagina">
    <h1><c:out value="${tituloPagina}"/></h1>
</div>

<c:if test="${not empty erros}">
    <div class="alerta alerta-erro" role="alert">
        <p><strong>Não foi possível salvar.</strong> Corrija os pontos abaixo:</p>
        <ul>
            <c:forEach var="erro" items="${erros}">
                <li><c:out value="${erro}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<c:url var="acaoFormulario" value="${modoEdicao ? '/itens/editar' : '/itens/novo'}"/>
<form class="painel formulario" method="post" action="${acaoFormulario}">
    <c:if test="${modoEdicao}">
        <input type="hidden" name="id" value="${itemId}">
    </c:if>

    <div class="campo campo-inteiro">
        <label for="titulo">Título <span class="obrigatorio" aria-hidden="true">*</span></label>
        <input type="text" id="titulo" name="titulo" maxlength="150" required
               value="<c:out value='${form.titulo}'/>">
    </div>

    <div class="campo">
        <label for="autorDiretor">Autor/Diretor <span class="obrigatorio" aria-hidden="true">*</span></label>
        <input type="text" id="autorDiretor" name="autorDiretor" maxlength="100" required
               value="<c:out value='${form.autorDiretor}'/>">
        <small>Autor, para livros; diretor ou criador, para filmes e séries.</small>
    </div>

    <div class="campo">
        <label for="tipoMidia">Tipo de mídia <span class="obrigatorio" aria-hidden="true">*</span></label>
        <select id="tipoMidia" name="tipoMidia" required>
            <option value="">Selecione…</option>
            <c:forEach var="t" items="${tipos}">
                <option value="${t.name()}" ${t.name() eq form.tipoMidia ? 'selected' : ''}>
                    <c:out value="${t.descricao}"/>
                </option>
            </c:forEach>
        </select>
    </div>

    <div class="campo">
        <label for="anoLancamento">Ano de lançamento <span class="obrigatorio" aria-hidden="true">*</span></label>
        <input type="text" id="anoLancamento" name="anoLancamento" inputmode="numeric" maxlength="4"
               required placeholder="Ex.: 1999" value="<c:out value='${form.anoLancamento}'/>">
    </div>

    <div class="campo">
        <label for="genero">Gênero</label>
        <input type="text" id="genero" name="genero" maxlength="50"
               placeholder="Ex.: Ficção científica" value="<c:out value='${form.genero}'/>">
    </div>

    <div class="campo campo-inteiro">
        <label for="sinopse">Sinopse</label>
        <textarea id="sinopse" name="sinopse" rows="5" maxlength="1000"><c:out value="${form.sinopse}"/></textarea>
        <small>Opcional. Até 1000 caracteres.</small>
    </div>

    <div class="acoes-formulario campo-inteiro">
        <button type="submit" class="botao">${modoEdicao ? 'Salvar alterações' : 'Cadastrar'}</button>
        <a class="botao botao-secundario" href="<c:url value='/itens'/>">Cancelar</a>
        <span class="legenda"><span class="obrigatorio">*</span> campos obrigatórios</span>
    </div>
</form>

<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
