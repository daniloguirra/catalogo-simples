package br.com.catalogo.dao;

public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
