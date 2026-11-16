package br.com.catalogo.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class FabricaDeConexoes {

    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

    private static final String URL;
    private static final String USUARIO;
    private static final String SENHA;

    static {
        Properties props = new Properties();
        try (InputStream in = FabricaDeConexoes.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Não foi possível ler db.properties: " + e.getMessage());
        }

        URL = valor("CATALOGO_DB_URL", props, "db.url");
        USUARIO = valor("CATALOGO_DB_USUARIO", props, "db.usuario");
        SENHA = valor("CATALOGO_DB_SENHA", props, "db.senha");

        try {
            Class.forName(DRIVER_MYSQL);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver JDBC do MySQL não encontrado no classpath.");
        }
    }

    private FabricaDeConexoes() {
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String valor(String variavelAmbiente, Properties props, String chave) {
        String doAmbiente = System.getenv(variavelAmbiente);
        if (doAmbiente != null && !doAmbiente.isBlank()) {
            return doAmbiente;
        }
        return props.getProperty(chave, "");
    }
}
