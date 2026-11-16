package br.com.catalogo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TipoMidiaTest {

    @Test
    void converteTextoValidoSemDiferenciarMaiusculas() {
        assertEquals(TipoMidia.FILME, TipoMidia.deTexto("filme"));
        assertEquals(TipoMidia.SERIE, TipoMidia.deTexto(" SERIE "));
        assertEquals(TipoMidia.LIVRO, TipoMidia.deTexto("Livro"));
    }

    @Test
    void retornaNullParaTextoVazioOuInvalido() {
        assertNull(TipoMidia.deTexto(null));
        assertNull(TipoMidia.deTexto(""));
        assertNull(TipoMidia.deTexto("REVISTA"));
        assertNull(TipoMidia.deTexto("' OR 1=1 --"));
    }

    @Test
    void descricaoAmigavel() {
        assertEquals("Série", TipoMidia.SERIE.getDescricao());
    }
}
