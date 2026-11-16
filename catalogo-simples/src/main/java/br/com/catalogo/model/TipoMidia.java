package br.com.catalogo.model;

public enum TipoMidia {

    LIVRO("Livro"),
    SERIE("Série"),
    FILME("Filme");

    private final String descricao;

    TipoMidia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoMidia deTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        for (TipoMidia tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor.trim())) {
                return tipo;
            }
        }
        return null;
    }
}
