package negocio;

import enums.TipoUnidad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.UnidadAcademica;

public class NodoUnidad {

    private final UnidadAcademica dato;
    private final List<NodoUnidad> hijos;
    private final ArbolEstudiantes estudiantes;

    public NodoUnidad(UnidadAcademica dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El nodo necesita una unidad académica");
        }
        this.dato = dato;
        this.hijos = new ArrayList<>();
        this.estudiantes = dato.getTipo() == TipoUnidad.PARALELO ? new ArbolEstudiantes() : null;
    }

    public UnidadAcademica getDato() {
        return dato;
    }

    public boolean agregarHijo(NodoUnidad hijo) {
        if (hijo == null || !dato.getTipo().puedeTenerHijo(hijo.getDato().getTipo())) {
            return false;
        }
        return hijos.add(hijo);
    }

    public boolean quitarHijo(NodoUnidad hijo) {
        return hijos.remove(hijo);
    }

    public List<NodoUnidad> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    public boolean esHoja() {
        return hijos.isEmpty();
    }

    public ArbolEstudiantes getEstudiantes() {
        return estudiantes;
    }
}
