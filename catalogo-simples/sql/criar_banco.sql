CREATE DATABASE IF NOT EXISTS catalogo_midia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE catalogo_midia;

CREATE TABLE IF NOT EXISTS item_midia (
    id              INT           NOT NULL AUTO_INCREMENT,
    titulo          VARCHAR(150)  NOT NULL,
    autor_diretor   VARCHAR(100)  NOT NULL,
    ano_lancamento  SMALLINT      NOT NULL,
    genero          VARCHAR(50)   NULL,
    sinopse         VARCHAR(1000) NULL,
    tipo_midia      ENUM('LIVRO', 'SERIE', 'FILME') NOT NULL,
    data_cadastro   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_item_midia PRIMARY KEY (id),
    CONSTRAINT ck_item_midia_titulo CHECK (CHAR_LENGTH(TRIM(titulo)) > 0),
    CONSTRAINT ck_item_midia_autor  CHECK (CHAR_LENGTH(TRIM(autor_diretor)) > 0),
    CONSTRAINT ck_item_midia_ano    CHECK (ano_lancamento BETWEEN 1000 AND 2100),

    INDEX idx_item_midia_titulo (titulo),
    INDEX idx_item_midia_tipo (tipo_midia)
) ENGINE = InnoDB;

CREATE USER IF NOT EXISTS 'catalogo_app'@'localhost' IDENTIFIED BY 'Catalogo@2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON catalogo_midia.item_midia TO 'catalogo_app'@'localhost';
FLUSH PRIVILEGES;
