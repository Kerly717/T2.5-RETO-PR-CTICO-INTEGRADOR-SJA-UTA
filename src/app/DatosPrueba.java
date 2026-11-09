package app;

import enums.TipoUnidad;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;

public final class DatosPrueba {
    private DatosPrueba() { }

    public static UnidadAcademica universidad() {
        return new UnidadAcademica("UTA", "Universidad Técnica de Ambato", TipoUnidad.UNIVERSIDAD);
    }

    public static void cargar(ArbolOrganizacional arbol) {
        arbol.agregar("UTA", new UnidadAcademica("FCHE", "Ciencias Humanas y de la Educación", TipoUnidad.FACULTAD));
        arbol.agregar("FCHE", new UnidadAcademica("EB", "Educación Básica", TipoUnidad.CARRERA));
        arbol.agregar("EB", new UnidadAcademica("EB-N1", "Nivel 1", TipoUnidad.NIVEL));
        arbol.agregar("EB-N1", new UnidadAcademica("EB-N1-A", "Paralelo A", TipoUnidad.PARALELO));
        arbol.agregar("EB-N1", new UnidadAcademica("EB-N1-B", "Paralelo B", TipoUnidad.PARALELO));
        ArbolEstudiantes abb = arbol.buscar("EB-N1-A").getEstudiantes();
        int[] matriculas = {30, 15, 45, 10, 20, 40, 50, 35, 55};
        for (int matricula : matriculas) {
            abb.insertar(new Estudiante(matricula,
                    String.format("%010d", matricula), "Estudiante " + matricula, 8.00));
        }
    }
}
