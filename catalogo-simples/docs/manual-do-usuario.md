# Manual do Usuário: Catálogo Simples

O Catálogo Simples guarda e organiza **livros, séries e filmes**.
Com ele você cadastra obras, consulta a lista, faz buscas, corrige informações e remove itens.

## 1. Instalação (resumo)

1. Instale o **MySQL 8**, o **JDK 17** e o **Apache Tomcat 10.1**.
2. No MySQL, execute o arquivo `sql/criar_banco.sql`. Para começar com exemplos, execute também `sql/dados_exemplo.sql`.
3. Coloque o arquivo `catalogo-simples.war` na pasta `webapps` do Tomcat e inicie o Tomcat.
4. No navegador, acesse **http://localhost:8080/catalogo-simples/**.

> Se a senha do banco for diferente da padrão, ajuste `db.properties`. O passo a passo completo está no `README.md`.

## 2. Tela inicial: lista de itens

Ao abrir o sistema, você vê todos os itens em ordem alfabética.
Cada linha mostra **título, autor/diretor, ano, gênero e tipo** (Livro, Série ou Filme, cada um com uma cor).
Para ler a sinopse de um item, clique em **Sinopse**, logo abaixo do título.

No menu superior: **Itens** volta para a lista, e **Novo item** abre o cadastro.

## 3. Cadastrar um item

1. Clique em **+ Novo item**.
2. Preencha os campos. Os marcados com **\*** são obrigatórios:
   - **Título\*** (até 150 caracteres)
   - **Autor/Diretor\***: o autor, para livros; o diretor ou criador, para filmes e séries (até 100 caracteres)
   - **Tipo de mídia\***: Livro, Série ou Filme
   - **Ano de lançamento\***: apenas números, por exemplo `1999`
   - **Gênero** (opcional, até 50 caracteres)
   - **Sinopse** (opcional, até 1000 caracteres)
3. Clique em **Cadastrar**.

Se algo estiver errado, o sistema mostra uma caixa vermelha listando **todos** os problemas, e o que você já digitou continua preenchido.
Se der certo, você volta à lista com a mensagem verde **"Item cadastrado com sucesso"**.

## 4. Buscar

No painel de busca, acima da lista:

- digite parte do **título** ou do **nome do autor/diretor**. Maiúsculas, minúsculas e acentos não fazem diferença (`duna` encontra "Duna");
- se quiser, escolha um **Tipo** para ver só livros, só séries ou só filmes;
- clique em **Buscar**. Para voltar à lista completa, clique em **Limpar**.

Deixando o texto em branco e escolhendo apenas um tipo, você vê todos os itens daquele tipo.

## 5. Editar

Na linha do item, clique em **Editar**. O formulário abre com os dados atuais.
Altere o que quiser e clique em **Salvar alterações**, ou em **Cancelar** para desistir.

## 6. Excluir

Na linha do item, clique em **Excluir** e confirme na janela que aparece.
**A exclusão é definitiva.**

## 7. Problemas comuns

| Situação | O que fazer |
|---|---|
| Página "Algo deu errado" | Verifique se o MySQL está ligado e se usuário/senha em `db.properties` estão corretos |
| Página "Página não encontrada" | O item foi excluído ou o endereço foi digitado errado; volte ao catálogo |
| Acentos aparecendo estranhos | Confirme que o banco foi criado com o script fornecido (utf8mb4) |
