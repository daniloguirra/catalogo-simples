# Catálogo Simples de Livros, Séries e Filmes

Aplicação web acadêmica que faz o CRUD (cadastrar, listar, editar e excluir) e a busca de itens de mídia.
Ela foi construída com **Java 17, Servlets, JSP/JSTL e JDBC**, sobre **MySQL 8** e **Tomcat 10.1**.

O projeto mostra a refatoração de um código "tudo dentro da Servlet" para uma arquitetura em camadas,
com Orientação a Objetos, classes **DAO** e classes de **serviço**.

---

## 1. Arquitetura

```
 Navegador
    │  HTTP (GET/POST)
    ▼
┌──────────────────────────┐
│ Servlets (controller)    │  Leem request.getParameter(), chamam o serviço, escolhem a JSP
│  ItemMidiaServletBase    │  ← superclasse com o código comum (herança)
│  Listar/Buscar/Cadastrar │
│  Editar/Excluir          │
└────────────┬─────────────┘
             ▼
┌──────────────────────────┐
│ ItemMidiaService         │  Regras de negócio e validação (sem HTTP e sem SQL)
└────────────┬─────────────┘
             ▼
┌──────────────────────────┐
│ ItemMidiaDAO (interface) │  Contrato de acesso a dados (polimorfismo)
│ ItemMidiaDAOJdbc         │  JDBC + PreparedStatement
│ FabricaDeConexoes        │  Único lugar que abre conexões
└────────────┬─────────────┘
             ▼
          MySQL (tabela item_midia)

 JSPs (view): listaItens.jsp, formularioItem.jsp, erro.jsp, com EL e JSTL (<c:out> escapa HTML)
 ItemMidia / TipoMidia (model): objetos de domínio, sem dependência de HTTP nem de banco
```

| Camada | Pacote / pasta | Responsabilidade |
|---|---|---|
| Model | `br.com.catalogo.model` | `ItemMidia` (POJO encapsulado) e `TipoMidia` (enum LIVRO/SERIE/FILME) |
| DAO | `br.com.catalogo.dao` | `ItemMidiaDAO` (interface), `ItemMidiaDAOJdbc`, `FabricaDeConexoes`, `DAOException` |
| Service | `br.com.catalogo.service` | `ItemMidiaService` (validação e regras), `ItemMidiaForm`, `ValidacaoException` |
| Controller | `br.com.catalogo.controller` | Uma Servlet por ação, todas herdando de `ItemMidiaServletBase` |
| View | `src/main/webapp/WEB-INF/views` | JSPs protegidas dentro de `WEB-INF`, com fragmentos de cabeçalho e rodapé |

### Rotas

| Método | URL | Servlet | Ação |
|---|---|---|---|
| GET | `/itens` | `ListarItensServlet` | Lista todos os itens |
| GET | `/itens/buscar?q=&tipo=` | `BuscarItensServlet` | Busca por título ou autor/diretor, com filtro de tipo |
| GET / POST | `/itens/novo` | `CadastrarItemServlet` | Formulário e gravação de um novo item |
| GET / POST | `/itens/editar?id=` | `EditarItemServlet` | Formulário preenchido e gravação da edição |
| POST | `/itens/excluir` | `ExcluirItemServlet` | Exclusão (só por POST, com confirmação) |

### Estrutura de pastas

```
catalogo-simples/
├── pom.xml
├── sql/
│   ├── criar_banco.sql        banco, tabela, restrições e usuário da aplicação
│   └── dados_exemplo.sql      9 itens para testar (opcional)
├── docs/manual-do-usuario.md
└── src/
    ├── main/java/br/com/catalogo/{model,dao,service,controller}
    ├── main/resources/db.properties
    ├── main/webapp/{index.jsp, css/, WEB-INF/web.xml, WEB-INF/views/}
    └── test/java/br/com/catalogo/   testes JUnit 5
```

---

## 2. Pré-requisitos

| Software | Versão | Observação |
|---|---|---|
| JDK | 17 ou superior | ex.: Eclipse Temurin 17 |
| Maven | 3.9+ | já vem embutido no Eclipse, IntelliJ e NetBeans |
| MySQL Server | 8.0+ | com MySQL Workbench, se preferir interface gráfica |
| Apache Tomcat | **10.1.x** | precisa ser 10+, porque o projeto usa `jakarta.servlet` |

## 3. Instalação e execução

**1) Criar o banco.** Como administrador do MySQL (ex.: `root`):

```bash
mysql -u root -p < sql/criar_banco.sql
mysql -u root -p < sql/dados_exemplo.sql    # opcional
```

Também dá para abrir os arquivos no MySQL Workbench e executar.

**2) Conferir as credenciais** em `src/main/resources/db.properties`.
O script cria o usuário `catalogo_app` com a senha `Catalogo@2026`. Se trocar a senha no SQL, troque aqui também.
Em vez de editar o arquivo, você pode definir as variáveis de ambiente
`CATALOGO_DB_URL`, `CATALOGO_DB_USUARIO` e `CATALOGO_DB_SENHA`, que têm prioridade.

**3) Gerar o pacote e publicar no Tomcat:**

```bash
mvn clean package
```

Copie `target/catalogo-simples.war` para a pasta `webapps/` do Tomcat e inicie o servidor
(`bin/startup.bat` no Windows). Depois acesse **http://localhost:8080/catalogo-simples/**.

> **Pela IDE:** importe a pasta como *projeto Maven existente* e adicione um servidor Tomcat 10.1.
> Depois use *Run on Server* (Eclipse IDE for Enterprise Java) ou *Run* (NetBeans).

## 4. Testes

### Testes unitários (não precisam de banco)

```bash
mvn test
```

- `ItemMidiaServiceTest`: validação (obrigatórios, ano numérico e na faixa, tamanhos máximos, tipo válido), cadastro, busca, edição e exclusão. Usa o `FakeItemMidiaDAO`, um DAO em memória.
- `TipoMidiaTest`: conversão de texto para o enum.

### Teste de integração do DAO (precisa do MySQL rodando)

O `ItemMidiaDAOJdbcTest` só roda se a variável `CATALOGO_DB_URL` estiver definida. Sem ela, o teste é ignorado.
Ele cobre o CRUD completo, a busca, a resistência a SQL Injection e o escape dos curingas `%` e `_`.
Cada teste apaga os registros que criou.

```powershell
# PowerShell
$env:CATALOGO_DB_URL = "jdbc:mysql://localhost:3306/catalogo_midia?serverTimezone=America/Sao_Paulo&useSSL=false&allowPublicKeyRetrieval=true"
mvn test
```

### Roteiro de testes funcionais (manuais, no navegador)

| # | Passo | Resultado esperado |
|---|---|---|
| 1 | Acessar `/catalogo-simples/` | Redireciona para a listagem |
| 2 | *Novo item* → preencher tudo → *Cadastrar* | Volta à lista com "Item cadastrado com sucesso" e o item aparece |
| 3 | Pressionar F5 logo após cadastrar | Não duplica o item (Post/Redirect/Get) |
| 4 | Buscar parte do título cadastrado | O item aparece no resultado |
| 5 | Buscar pelo nome do autor e filtrar por tipo | Só aparecem itens daquele autor e tipo |
| 6 | Cadastrar com título só com espaços e ano `abc` | O formulário volta com os erros e mantém o que foi digitado |
| 7 | Cadastrar com ano `3000` | Erro "deve estar entre 1000 e …" |
| 8 | Cadastrar título `<script>alert(1)</script>` | Aparece como texto na lista; nenhum alerta é executado (XSS bloqueado) |
| 9 | Buscar `' OR '1'='1` | Nenhum resultado e nenhum erro (SQL Injection bloqueada) |
| 10 | *Editar* → mudar o ano → *Salvar alterações* | Mensagem de sucesso e ano atualizado |
| 11 | Acessar `/itens/editar?id=99999` | Página "Página não encontrada" |
| 12 | *Excluir* → confirmar | Mensagem de sucesso e item removido |
| 13 | Parar o MySQL e abrir a lista | Página de erro amigável, sem stack trace |

## 5. Segurança aplicada

- **SQL Injection:** todo SQL usa `PreparedStatement` com parâmetros `?`. Na busca dinâmica, só trechos fixos do código entram no texto do SQL.
- **Curingas do LIKE:** `%` e `_` digitados são escapados e procurados literalmente.
- **Validação no servidor** (`ItemMidiaService`): campos obrigatórios, ano numérico e na faixa de 1000 até o ano atual + 5, tamanhos máximos e tipo válido. O `maxlength`/`required` do HTML só ajuda o usuário e não substitui essa validação.
- **Restrições no banco** como segunda barreira: `NOT NULL`, tamanhos de `VARCHAR`, `ENUM` e `CHECK`.
- **XSS:** todo dado do usuário é exibido com `<c:out>`, que escapa `< > & ' "`.
- **Exclusão só por POST** com confirmação; um GET em `/itens/excluir` recebe 405.
- **JSPs dentro de `WEB-INF`**, sem acesso direto pela URL.
- **Páginas de erro genéricas:** o stack trace fica só no log do Tomcat.
- **Usuário de banco com menor privilégio** (`catalogo_app`: só SELECT/INSERT/UPDATE/DELETE na tabela). A senha pode vir de variável de ambiente.
- **Cookie de sessão `HttpOnly`.**

Trabalhos futuros: autenticação de usuários, token anti-CSRF nos formulários, HTTPS, paginação da listagem
e pool de conexões (DataSource/JNDI).
