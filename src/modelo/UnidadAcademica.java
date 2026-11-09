package modelo;

import enums.TipoUnidad;

public class UnidadAcademica {

    private final String codigo;
    private String nombre;
    private final TipoUnidad tipo;

    public UnidadAcademica(String codigo, String nombre, TipoUnidad tipo) {
        this.codigo = validarTexto(codigo, "código").toUpperCase();
        this.nombre = validarTexto(nombre, "nombre");
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de unidad es obligatorio");
        }
        this.tipo = tipo;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " no puede estar vacío");
        }
        return valor.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "nombre");
    }

    public TipoUnidad getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + tipo + ")";
    }
}
