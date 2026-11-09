package negocio;

import enums.TipoRecorrido;
import java.util.List;
import modelo.Estudiante;
import java.util.ArrayList;

public class ArbolEstudiantes {

    private NodoEstudiante raiz;

    public ArbolEstudiantes() {
        this.raiz = null;
    }

    public NodoEstudiante getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    // B1: inserta un estudiante sin matriculas repetidas (recursivo, O(h))
    public boolean insertar(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser null");
        }
        if (buscar(estudiante.getMatricula()) != null) {
            return false; // matricula repetida
        }
        raiz = insertar(raiz, estudiante);
        return true;
    }

    private NodoEstudiante insertar(NodoEstudiante nodo, Estudiante estudiante) {
        if (nodo == null) {
            return new NodoEstudiante(estudiante); // caso base: lugar libre
        }
        if (estudiante.getMatricula() < nodo.getDato().getMatricula()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), estudiante)); // baja a la izquierda
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), estudiante)); // baja a la derecha
        }
        return nodo;
    }

    // B2: busca por matricula (recursivo, O(h))
    public Estudiante buscar(int matricula) {
        return buscar(raiz, matricula);
    }

    private Estudiante buscar(NodoEstudiante nodo, int matricula) {
        if (nodo == null) {
            return null; // caso base: no existe
        }
        int actual = nodo.getDato().getMatricula();
        if (matricula == actual) {
            return nodo.getDato(); // caso base: encontrado
        }
        return matricula < actual
                ? buscar(nodo.getIzquierdo(), matricula)
                : buscar(nodo.getDerecho(), matricula);
    }

        // recorridos recursivos (O(n))
    public List<Estudiante> recorrer(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de recorrido es obligatorio");
        }
        List<Estudiante> lista = new ArrayList<>();
        switch (tipo) {
            case PREORDEN -> preorden(raiz, lista);
            case INORDEN -> inorden(raiz, lista);
            case POSORDEN -> posorden(raiz, lista);
        }
        return lista; // vacia si el arbol esta vacio
    }

    // Preorden: raiz, izquierdo, derecho
    private void preorden(NodoEstudiante nodo, List<Estudiante> lista) {
        if (nodo == null) return; // caso base
        lista.add(nodo.getDato());
        preorden(nodo.getIzquierdo(), lista);
        preorden(nodo.getDerecho(), lista);
    }

    // Inorden: izquierdo, raiz, derecho (queda ordenado por matricula)
    private void inorden(NodoEstudiante nodo, List<Estudiante> lista) {
        if (nodo == null) return;
        inorden(nodo.getIzquierdo(), lista);
        lista.add(nodo.getDato());
        inorden(nodo.getDerecho(), lista);
    }

    // Posorden: izquierdo, derecho, raiz
    private void posorden(NodoEstudiante nodo, List<Estudiante> lista) {
        if (nodo == null) return;
        posorden(nodo.getIzquierdo(), lista);
        posorden(nodo.getDerecho(), lista);
        lista.add(nodo.getDato());

        
    }
        // Traza del recorrido: una linea por paso, sangria por nivel
    public String trazaRecorrido(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de recorrido es obligatorio");
        }
        StringBuilder traza = new StringBuilder();
        StringBuilder secuencia = new StringBuilder();
        traza(raiz, tipo, 0, traza, secuencia);
        traza.append("Secuencia ").append(tipo).append(": ").append(secuencia.toString().trim());
        return traza.toString();
    }

    // Recursion de la traza: entra, null -> regresa y VISITA segun el tipo
    private void traza(NodoEstudiante nodo, TipoRecorrido tipo, int nivel, StringBuilder traza, StringBuilder secuencia) {
        String sangria = "  ".repeat(nivel);
        if (nodo == null) {
            traza.append(sangria).append("null -> regresa\n");
            return;
        }
        int m = nodo.getDato().getMatricula();
        traza.append(sangria).append("entra ").append(m).append("\n");
        if (tipo == TipoRecorrido.PREORDEN) visitar(m, sangria, traza, secuencia);
        traza(nodo.getIzquierdo(), tipo, nivel + 1, traza, secuencia);
        if (tipo == TipoRecorrido.INORDEN) visitar(m, sangria, traza, secuencia);
        traza(nodo.getDerecho(), tipo, nivel + 1, traza, secuencia);
        if (tipo == TipoRecorrido.POSORDEN) visitar(m, sangria, traza, secuencia);
    }

    // Registra la visita en la traza y en la secuencia
    private void visitar(int matricula, String sangria, StringBuilder traza, StringBuilder secuencia) {
        traza.append(sangria).append("VISITA ").append(matricula).append("\n");
        secuencia.append(matricula).append(" ");
    }

    /** Modifica cedula, nombres y promedio. La matricula no cambia. */
    public boolean modificar(int matricula, String cedula, String nombres, double promedio) {
        NodoEstudiante nodo = buscarNodo(raiz, matricula);
        if (nodo == null) {
            return false;
        }
        // Primero validar todo con un Estudiante temporal (lanza excepcion si hay datos invalidos)
        new Estudiante(matricula, cedula, nombres, promedio);
        // Solo despues de validar se cambian los datos del estudiante real
        Estudiante e = nodo.getDato();
        e.setCedula(cedula);
        e.setNombres(nombres);
        e.setPromedio(promedio);
        return true;
    }

    // Busqueda propia del nodo
    private NodoEstudiante buscarNodo(NodoEstudiante nodo, int matricula) {
        if (nodo == null) {                       // caso base: no existe
            return null;
        }
        int actual = nodo.getDato().getMatricula();
        if (matricula == actual) {                // caso base: encontrado
            return nodo;
        }
        if (matricula < actual) {                 // caso recursivo: izquierda
            return buscarNodo(nodo.getIzquierdo(), matricula);
        }
        return buscarNodo(nodo.getDerecho(), matricula); // caso recursivo: derecha
    }

    /** Elimina un estudiante por matricula. false si no existe. */
    public boolean eliminar(int matricula) {
        if (buscarNodo(raiz, matricula) == null) {
            return false;
        }
        raiz = eliminar(raiz, matricula);
        return true;
    }

    private NodoEstudiante eliminar(NodoEstudiante nodo, int matricula) {
        if (nodo == null) {                       // caso base: no se encontro
            return nodo;
        }
        int actual = nodo.getDato().getMatricula();
        if (matricula < actual) {                 // caso recursivo: izquierda
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), matricula));
        } else if (matricula > actual) {          // caso recursivo: derecha
            nodo.setDerecho(eliminar(nodo.getDerecho(), matricula));
        } else {                                  // nodo encontrado
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;                      // caso 1: hoja
            }
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();         // caso 2: solo hijo derecho
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();       // caso 2: solo hijo izquierdo
            }
            // caso 3: dos hijos -> sucesor inorden (menor del subarbol derecho)
            Estudiante sucesor = minimo(nodo.getDerecho());
            nodo.setDato(sucesor);
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getMatricula()));
        }
        return nodo;
    }

    // Menor del subarbol: baja siempre por la izquierda
    private Estudiante minimo(NodoEstudiante nodo) {
        if (nodo.getIzquierdo() == null) {        // caso base
            return nodo.getDato();
        }
        return minimo(nodo.getIzquierdo());       // caso recursivo
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoEstudiante nodo) {
        return nodo == null ? -1 : 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    public int contar() {
        return contar(raiz);
    }

    private int contar(NodoEstudiante nodo) {
        return nodo == null ? 0 : 1 + contar(nodo.getIzquierdo()) + contar(nodo.getDerecho());
    }

    public Estudiante menor() {
        NodoEstudiante nodo = raiz;
        if (nodo == null) return null;
        while (nodo.getIzquierdo() != null) nodo = nodo.getIzquierdo();
        return nodo.getDato();
    }

    public Estudiante mayor() {
        NodoEstudiante nodo = raiz;
        if (nodo == null) return null;
        while (nodo.getDerecho() != null) nodo = nodo.getDerecho();
        return nodo.getDato();
    }

    public double promedioGeneral() {
        int total = contar();
        return total == 0 ? 0.0 : sumarPromedios(raiz) / total;
    }

    private double sumarPromedios(NodoEstudiante nodo) {
        return nodo == null ? 0.0 : nodo.getDato().getPromedio()
                + sumarPromedios(nodo.getIzquierdo()) + sumarPromedios(nodo.getDerecho());
    }

    public int contarAprobados(double notaMinima) {
        if (!Double.isFinite(notaMinima)) {
            throw new IllegalArgumentException("La nota mínima debe ser finita");
        }
        return contarAprobados(raiz, notaMinima);
    }

    private int contarAprobados(NodoEstudiante nodo, double notaMinima) {
        if (nodo == null) return 0;
        return (nodo.getDato().getPromedio() >= notaMinima ? 1 : 0)
                + contarAprobados(nodo.getIzquierdo(), notaMinima)
                + contarAprobados(nodo.getDerecho(), notaMinima);
    }
}
