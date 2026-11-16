package br.com.catalogo.model;

import java.util.Objects;

public class ItemMidia {

    private Integer id;
    private String titulo;
    private String autorDiretor;
    private Integer anoLancamento;
    private String genero;
    private String sinopse;
    private TipoMidia tipoMidia;

    public ItemMidia() {
    }

    public ItemMidia(Integer id, String titulo, String autorDiretor, Integer anoLancamento,
                     String genero, String sinopse, TipoMidia tipoMidia) {
        this.id = id;
        this.titulo = titulo;
        this.autorDiretor = autorDiretor;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
        this.sinopse = sinopse;
        this.tipoMidia = tipoMidia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutorDiretor() {
        return autorDiretor;
    }

    public void setAutorDiretor(String autorDiretor) {
        this.autorDiretor = autorDiretor;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public TipoMidia getTipoMidia() {
        return tipoMidia;
    }

    public void setTipoMidia(TipoMidia tipoMidia) {
        this.tipoMidia = tipoMidia;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ItemMidia)) {
            return false;
        }
        ItemMidia that = (ItemMidia) outro;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemMidia{id=" + id + ", titulo='" + titulo + "', tipo=" + tipoMidia
                + ", ano=" + anoLancamento + "}";
    }
}
