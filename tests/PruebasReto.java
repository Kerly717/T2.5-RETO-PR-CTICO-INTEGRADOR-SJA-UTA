import app.DatosPrueba;
import app.RetoIntegrador;
import enums.TipoRecorrido;
import modelo.Estudiante;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;

public class PruebasReto {
    private static int comprobaciones;

    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static ArbolOrganizacional inicial() {
        ArbolOrganizacional arbol = new ArbolOrganizacional(DatosPrueba.universidad());
        DatosPrueba.cargar(arbol);
        return arbol;
    }

    public static void main(String[] args) {
        ArbolOrganizacional arbol = inicial();
        ArbolEstudiantes a = arbol.buscar("EB-N1-A").getEstudiantes();
        ArbolEstudiantes b = arbol.buscar("EB-N1-B").getEstudiantes();
        verificar(a.contar() == 9 && b.estaVacio(), "Carga inicial");
        verificar(a.altura() == 3 && b.altura() == -1, "Alturas iniciales en aristas");
        verificar(arbol.altura() == 4 && arbol.contarHojas() == 2, "Árbol general inicial");
        verificar(arbol.obtenerRutaJerarquica("EB-N1-A").equals("UTA -> FCHE -> EB -> EB-N1 -> EB-N1-A"), "Ruta");
        verificar(arbol.obtenerRutaJerarquica("nada").equals("No existe"), "Ruta inexistente");
        verificar(a.contarAprobados(8) == 9 && b.contarAprobados(8) == 0, "Aprobados iniciales");
        verificar(b.menor() == null && b.mayor() == null && b.promedioGeneral() == 0.0, "Estadísticas vacías");
        String primera = RetoIntegrador.ejecutar(arbol);
        verificar(primera.contains("Traslado 10: hoja") && primera.contains("Traslado 40: un hijo")
                && primera.contains("Traslado 45: dos hijos") && primera.contains("Traslado 30: raíz; dos hijos"), "Casos de eliminación");
        verificar(a.contar() == 5 && b.contar() == 4, "Conteos finales");
        verificar(a.promedioGeneral() == 8.36 && b.promedioGeneral() == 8.0, "Promedios finales");
        verificar(a.contarAprobados(8) == 5 && b.contarAprobados(8) == 4, "Aprobados finales");
        verificar(a.buscar(35).getPromedio() == 9.8, "Actualización válida");
        verificar(a.buscar(10) == null && b.buscar(10) != null, "Traslado efectivo");
        verificar(arbol.buscar("SW-N3-A") != null && arbol.contarHojas() == 3, "Jerarquía final");
        String repetida = RetoIntegrador.ejecutar(arbol);
        verificar(repetida.contains("no se crearon duplicados") && a.contar() == 5 && b.contar() == 4, "Idempotencia");
        arbol = inicial();
        a = arbol.buscar("EB-N1-A").getEstudiantes();
        b = arbol.buscar("EB-N1-B").getEstudiantes();
        verificar(a.contar() == 9 && b.contar() == 0 && arbol.buscar("SW") == null, "Reinicio exacto");
        verificar(a.recorrer(TipoRecorrido.INORDEN).get(0).getMatricula() == 10, "Orden restaurado");
        boolean invalido = false;
        try { new Estudiante(-1, "0000000001", "Inválido", 8); }
        catch (IllegalArgumentException esperado) { invalido = true; }
        verificar(invalido, "Matrícula no positiva");
        System.out.println("APROBADAS: " + comprobaciones + " comprobaciones");
        System.out.println("ESTADO FINAL: A=15,20,35,50,55; B=10,30,40,45; promedios=8.36/8.00");
        System.out.println("REINICIO: A=9 matrículas; B=0; SW inexistente");
    }
}
