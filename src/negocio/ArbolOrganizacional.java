package negocio;

import enums.TipoUnidad;
import modelo.Estudiante;
import modelo.UnidadAcademica;

public class ArbolOrganizacional {
    private NodoUnidad raiz;

    public ArbolOrganizacional(UnidadAcademica universidad) {
        if (universidad == null || universidad.getTipo() != TipoUnidad.UNIVERSIDAD) {
            throw new IllegalArgumentException("La raíz debe ser una UNIVERSIDAD");
        }
        raiz = new NodoUnidad(universidad);
    }

    public NodoUnidad getRaiz() { return raiz; }

    public boolean agregar(String codigoPadre, UnidadAcademica unidad) {
        if (codigoPadre == null || unidad == null || buscar(unidad.getCodigo()) != null) return false;
        NodoUnidad padre = buscar(codigoPadre);
        return padre != null && padre.agregarHijo(new NodoUnidad(unidad));
    }

    public NodoUnidad buscar(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        return buscar(raiz, codigo.trim().toUpperCase());
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {
        if (nodo.getDato().getCodigo().equals(codigo)) return nodo;
        for (NodoUnidad hijo : nodo.getHijos()) {
            NodoUnidad encontrado = buscar(hijo, codigo);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    public String mostrarJerarquia() {
        StringBuilder salida = new StringBuilder();
        mostrarJerarquia(raiz, 0, salida);
        return salida.toString().stripTrailing();
    }

    private void mostrarJerarquia(NodoUnidad nodo, int profundidad, StringBuilder salida) {
        salida.append("  ".repeat(profundidad)).append(nodo.getDato()).append('\n');
        for (NodoUnidad hijo : nodo.getHijos()) mostrarJerarquia(hijo, profundidad + 1, salida);
    }

    public int altura() { return altura(raiz); }

    private int altura(NodoUnidad nodo) {
        int maximo = -1;
        for (NodoUnidad hijo : nodo.getHijos()) maximo = Math.max(maximo, altura(hijo));
        return maximo + 1;
    }

    public int grado(String codigo) {
        NodoUnidad nodo = buscar(codigo);
        return nodo == null ? -1 : nodo.getHijos().size();
    }

    public int nivel(String codigo) {
        if (codigo == null || codigo.isBlank()) return -1;
        return nivel(raiz, codigo.trim().toUpperCase(), 0);
    }

    private int nivel(NodoUnidad nodo, String codigo, int profundidad) {
        if (nodo.getDato().getCodigo().equals(codigo)) return profundidad;
        for (NodoUnidad hijo : nodo.getHijos()) {
            int encontrado = nivel(hijo, codigo, profundidad + 1);
            if (encontrado >= 0) return encontrado;
        }
        return -1;
    }

    public int contarHojas() { return contarHojas(raiz); }

    private int contarHojas(NodoUnidad nodo) {
        if (nodo.esHoja()) return 1;
        int total = 0;
        for (NodoUnidad hijo : nodo.getHijos()) total += contarHojas(hijo);
        return total;
    }

    public String obtenerRutaJerarquica(String codigo) {
        if (codigo == null || codigo.isBlank()) return "No existe";
        StringBuilder ruta = new StringBuilder();
        return construirRuta(raiz, codigo.trim().toUpperCase(), ruta) ? ruta.toString() : "No existe";
    }

    private boolean construirRuta(NodoUnidad nodo, String codigo, StringBuilder ruta) {
        int longitudAnterior = ruta.length();
        if (longitudAnterior > 0) ruta.append(" -> ");
        ruta.append(nodo.getDato().getCodigo());
        if (nodo.getDato().getCodigo().equals(codigo)) return true;
        for (NodoUnidad hijo : nodo.getHijos()) {
            if (construirRuta(hijo, codigo, ruta)) return true;
        }
        ruta.setLength(longitudAnterior);
        return false;
    }

    public boolean eliminar(String codigo) {
        NodoUnidad objetivo = buscar(codigo);
        if (objetivo == null || objetivo == raiz || !objetivo.esHoja()) return false;
        if (objetivo.getEstudiantes() != null && !objetivo.getEstudiantes().estaVacio()) return false;
        NodoUnidad padre = buscarPadre(raiz, objetivo);
        return padre != null && padre.quitarHijo(objetivo);
    }

    private NodoUnidad buscarPadre(NodoUnidad actual, NodoUnidad objetivo) {
        for (NodoUnidad hijo : actual.getHijos()) {
            if (hijo == objetivo) return actual;
            NodoUnidad padre = buscarPadre(hijo, objetivo);
            if (padre != null) return padre;
        }
        return null;
    }

    public boolean trasladarEstudiante(String codOrigen, String codDestino, int matricula) {
        NodoUnidad origen = buscar(codOrigen), destino = buscar(codDestino);
        if (origen == null || destino == null || origen == destino
                || origen.getDato().getTipo() != TipoUnidad.PARALELO
                || destino.getDato().getTipo() != TipoUnidad.PARALELO) return false;
        ArbolEstudiantes a = origen.getEstudiantes(), b = destino.getEstudiantes();
        Estudiante estudiante = a.buscar(matricula);
        if (estudiante == null || b.buscar(matricula) != null) return false;
        if (!b.insertar(estudiante)) return false;
        try {
            if (a.eliminar(matricula)) return true;
        } catch (RuntimeException fallo) {
            if (!b.eliminar(matricula)) {
                throw new IllegalStateException("No se pudo revertir un traslado incompleto", fallo);
            }
            throw fallo;
        }
        // Compensación: la inserción en destino se revierte si falla la extracción.
        if (!b.eliminar(matricula)) {
            throw new IllegalStateException("No se pudo revertir un traslado incompleto");
        }
        return false;
    }

    public String transformarABinario() {
        StringBuilder salida = new StringBuilder();
        transformarABinario(raiz, null, salida);
        return salida.toString().stripTrailing();
    }

    private void transformarABinario(NodoUnidad nodo, String hermanoDerecho, StringBuilder salida) {
        String izquierdo = nodo.getHijos().isEmpty() ? "null" : nodo.getHijos().get(0).getDato().getCodigo();
        salida.append(nodo.getDato().getCodigo()).append(" [izquierdo=").append(izquierdo)
                .append(", derecho=").append(hermanoDerecho == null ? "null" : hermanoDerecho)
                .append("]\n");
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            String siguiente = i + 1 < nodo.getHijos().size()
                    ? nodo.getHijos().get(i + 1).getDato().getCodigo() : null;
            transformarABinario(nodo.getHijos().get(i), siguiente, salida);
        }
    }
}
