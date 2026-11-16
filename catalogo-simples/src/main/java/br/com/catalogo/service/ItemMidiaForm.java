package br.com.catalogo.service;

import br.com.catalogo.model.ItemMidia;

public class ItemMidiaForm {

    private String titulo;
    private String autorDiretor;
    private String anoLancamento;
    private String genero;
    private String sinopse;
    private String tipoMidia;

    public static ItemMidiaForm de(ItemMidia item) {
        ItemMidiaForm form = new ItemMidiaForm();
        form.setTitulo(item.getTitulo());
        form.setAutorDiretor(item.getAutorDiretor());
        form.setAnoLancamento(item.getAnoLancamento() == null ? null : String.valueOf(item.getAnoLancamento()));
        form.setGenero(item.getGenero());
        form.setSinopse(item.getSinopse());
        form.setTipoMidia(item.getTipoMidia() == null ? null : item.getTipoMidia().name());
        return form;
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

    public String getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(String anoLancamento) {
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

    public String getTipoMidia() {
        return tipoMidia;
    }

    public void setTipoMidia(String tipoMidia) {
        this.tipoMidia = tipoMidia;
    }
}
