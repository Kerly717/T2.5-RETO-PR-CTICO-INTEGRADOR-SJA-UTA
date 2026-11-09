package negocio;

import modelo.Estudiante;

public class NodoEstudiante {

    private Estudiante dato;
    private NodoEstudiante izquierdo;
    private NodoEstudiante derecho;

    public NodoEstudiante(Estudiante dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El nodo necesita un estudiante");
        }
        this.dato = dato;
    }

    public Estudiante getDato() {
        return dato;
    }

    public void setDato(Estudiante dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El nodo necesita un estudiante");
        }
        this.dato = dato;
    }

    public NodoEstudiante getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoEstudiante izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoEstudiante getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoEstudiante derecho) {
        this.derecho = derecho;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}
