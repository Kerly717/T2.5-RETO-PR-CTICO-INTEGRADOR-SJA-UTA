package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.stream.Collectors;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;
import negocio.NodoEstudiante;
import negocio.NodoUnidad;

/** Ejecuta y comprueba el reto sobre un árbol recién reiniciado. */
public final class RetoIntegrador {
    private RetoIntegrador() { }

    public static String ejecutar(ArbolOrganizacional arbol) {
        NodoUnidad origen = arbol.buscar("EB-N1-A"), destino = arbol.buscar("EB-N1-B");
        if (origen == null || destino == null || origen.getEstudiantes() == null
                || destino.getEstudiantes() == null) {
            return "\nFaltan los paralelos base. Use la opción 7 para reiniciar antes de ejecutar el reto.\n";
        }
        ArbolEstudiantes a = origen.getEstudiantes();
        ArbolEstudiantes b = destino.getEstudiantes();
        StringBuilder s = new StringBuilder("\n=== RETO INTEGRADOR SJA-UTA ===\n");
        if (esEstadoFinal(arbol, a, b)) {
            s.append("El reto ya fue ejecutado. Estado final verificado; no se crearon duplicados.\n");
            estadisticas(s, "EB-N1-A", a);
            estadisticas(s, "EB-N1-B", b);
            return s.toString();
        }
        if (!esEstadoInicial(arbol, a, b)) {
            return "\nEl estado fue modificado. Use la opción 7 para reiniciar antes de ejecutar el reto.\n";
        }

        s.append("1. Jerarquía y ruta\n");
        comprobar(arbol.agregar("FCHE", new UnidadAcademica("SW", "Software", TipoUnidad.CARRERA)), "Agregar SW");
        comprobar(arbol.agregar("SW", new UnidadAcademica("SW-N3", "Nivel 3", TipoUnidad.NIVEL)), "Agregar SW-N3");
        comprobar(arbol.agregar("SW-N3", new UnidadAcademica("SW-N3-A", "Paralelo A", TipoUnidad.PARALELO)), "Agregar SW-N3-A");
        s.append(arbol.mostrarJerarquia()).append('\n');
        s.append("Nivel SW-N3-A: ").append(arbol.nivel("SW-N3-A"))
                .append(" | Altura: ").append(arbol.altura())
                .append(" | Hojas: ").append(arbol.contarHojas()).append('\n');
        s.append("Ruta EB-N1-A: ").append(arbol.obtenerRutaJerarquica("EB-N1-A")).append('\n');
        s.append("Ruta inexistente: ").append(arbol.obtenerRutaJerarquica("NO-EXISTE")).append('\n');
        comprobar("UTA -> FCHE -> EB -> EB-N1 -> EB-N1-A".equals(arbol.obtenerRutaJerarquica("EB-N1-A")), "Ruta");
        comprobar(arbol.altura() == 4 && arbol.contarHojas() == 3 && arbol.nivel("SW-N3-A") == 4, "Estadísticas de jerarquía");

        s.append("2. Protección y representación hijo izquierdo / hermano derecho\n");
        s.append("Eliminar EB-N1-A con estudiantes: ").append(arbol.eliminar("EB-N1-A")).append('\n');
        s.append("Eliminar SW-N3 con subunidad: ").append(arbol.eliminar("SW-N3")).append('\n');
        s.append("Eliminar código inexistente: ").append(arbol.eliminar("NO-EXISTE")).append('\n');
        comprobar(!arbol.eliminar("EB-N1-A") && !arbol.eliminar("SW-N3") && !arbol.eliminar("NO-EXISTE"), "Protección de unidades");
        comprobar(arbol.eliminar("SW-N3-A"), "Eliminar hoja vacía");
        s.append("Eliminar SW-N3-A vacío: true\n");
        comprobar(arbol.agregar("SW-N3", new UnidadAcademica("SW-N3-A", "Paralelo A", TipoUnidad.PARALELO)), "Restaurar paralelo para la comparación");
        String binario = arbol.transformarABinario();
        s.append(binario).append('\n');
        comprobar(binario.contains("EB [izquierdo=EB-N1, derecho=SW]")
                && binario.contains("EB-N1-A [izquierdo=null, derecho=EB-N1-B]"), "Relaciones entre hermanos");
        comprobar(arbol.grado("FCHE") == 2 && arbol.contarHojas() == 3, "Árbol general intacto");

        s.append("3. Validaciones y traslado\n");
        comprobar(rechaza(() -> new Estudiante(0, "0000000000", "Inválido", 8)), "Matrícula positiva");
        Estudiante temporal = new Estudiante(99, "0000000099", "Temporal", 8);
        comprobar(rechaza(() -> temporal.setPromedio(15))
                && rechaza(() -> temporal.setPromedio(Double.NaN))
                && rechaza(() -> temporal.setPromedio(Double.POSITIVE_INFINITY))
                && temporal.getPromedio() == 8, "Promedio válido y finito");
        comprobar(!arbol.trasladarEstudiante("FCHE", "EB-N1-B", 10)
                && !arbol.trasladarEstudiante("EB-N1-A", "NO-EXISTE", 10)
                && !arbol.trasladarEstudiante("EB-N1-A", "EB-N1-A", 10)
                && !arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 999), "Rechazos de traslado");
        s.append("Matrícula 0, notas 15/NaN/Infinity y traslados inválidos: rechazados\n");

        s.append("4. Modificación y eliminaciones del ABB\n");
        Estudiante e35 = a.buscar(35);
        comprobar(a.modificar(35, e35.getCedula(), e35.getNombres(), 9.80), "Actualizar promedio 35");
        comprobar(rechaza(() -> a.modificar(35, "incorrecta", e35.getNombres(), 9.80))
                && rechaza(() -> a.modificar(35, e35.getCedula(), e35.getNombres(), 15))
                && a.buscar(35) == e35 && e35.getPromedio() == 9.80, "Actualización atómica");
        s.append("Matrícula 35: promedio 9.80; cédula inválida y promedio 15 no cambiaron el registro\n");
        s.append("A inorden antes: ").append(inorden(a)).append('\n');
        int[] traslados = {10, 40, 45, 30};
        for (int matricula : traslados) {
            String caso = casoEliminacion(a.getRaiz(), matricula);
            comprobar(arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", matricula), "Trasladar " + matricula);
            s.append("Traslado ").append(matricula).append(": ").append(caso)
                    .append(" | A: ").append(inorden(a)).append(" | B: ").append(inorden(b)).append('\n');
        }
        comprobar(!arbol.trasladarEstudiante("EB-N1-B", "EB-N1-A", 35), "Matrícula inexistente en origen");
        comprobar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 10), "Traslado repetido");
        // Una misma matrícula en ambos paralelos se rechaza sin tocar ninguno.
        comprobar(a.insertar(new Estudiante(10, "1000000010", "Duplicado de prueba", 8)), "Preparar duplicado");
        comprobar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 10)
                && a.buscar(10) != null && b.buscar(10) != null, "Destino duplicado");
        comprobar(a.eliminar(10), "Retirar duplicado de prueba");
        s.append("Destino duplicado y traslado repetido: rechazados sin pérdida\n");

        s.append("5. Estadísticas finales (nota mínima 8.00)\n");
        estadisticas(s, "EB-N1-A", a);
        estadisticas(s, "EB-N1-B", b);
        comprobar(esEstadoFinal(arbol, a, b), "Estado final esperado");
        s.append("El inorden visita izquierda, nodo y derecha; el ABB mantiene menores a la izquierda y mayores a la derecha.\n");
        s.append("6. Ejecute la opción 7 para reconstruir el estado inicial.\n");
        return s.toString();
    }

    private static boolean esEstadoInicial(ArbolOrganizacional arbol, ArbolEstudiantes a, ArbolEstudiantes b) {
        if (arbol.buscar("SW") != null || arbol.contarHojas() != 2 || b.contar() != 0) return false;
        if (!inorden(a).equals("10, 15, 20, 30, 35, 40, 45, 50, 55")) return false;
        for (Estudiante e : a.recorrer(TipoRecorrido.INORDEN)) if (e.getPromedio() != 8.0) return false;
        return true;
    }

    private static boolean esEstadoFinal(ArbolOrganizacional arbol, ArbolEstudiantes a, ArbolEstudiantes b) {
        return arbol.buscar("SW-N3-A") != null
                && inorden(a).equals("15, 20, 35, 50, 55")
                && inorden(b).equals("10, 30, 40, 45")
                && a.buscar(35).getPromedio() == 9.80
                && Math.abs(a.promedioGeneral() - 8.36) < 1e-9
                && Math.abs(b.promedioGeneral() - 8.00) < 1e-9;
    }

    private static String inorden(ArbolEstudiantes abb) {
        return abb.recorrer(TipoRecorrido.INORDEN).stream()
                .map(e -> Integer.toString(e.getMatricula())).collect(Collectors.joining(", "));
    }

    private static String casoEliminacion(NodoEstudiante nodo, int matricula) {
        NodoEstudiante raiz = nodo;
        while (nodo != null && nodo.getDato().getMatricula() != matricula) {
            nodo = matricula < nodo.getDato().getMatricula() ? nodo.getIzquierdo() : nodo.getDerecho();
        }
        if (nodo == null) return "no encontrado";
        int hijos = (nodo.getIzquierdo() == null ? 0 : 1) + (nodo.getDerecho() == null ? 0 : 1);
        return (nodo == raiz ? "raíz; " : "") + (hijos == 0 ? "hoja" : hijos == 1 ? "un hijo" : "dos hijos");
    }

    private static void estadisticas(StringBuilder s, String codigo, ArbolEstudiantes abb) {
        s.append(codigo).append(" | inorden: ").append(inorden(abb))
                .append(" | cantidad: ").append(abb.contar())
                .append(" | altura: ").append(abb.altura())
                .append(" | menor: ").append(abb.menor() == null ? "—" : abb.menor().getMatricula())
                .append(" | mayor: ").append(abb.mayor() == null ? "—" : abb.mayor().getMatricula())
                .append(String.format(java.util.Locale.ROOT, " | promedio: %.2f", abb.promedioGeneral()))
                .append(" | aprobados: ").append(abb.contarAprobados(8.00)).append('\n');
    }

    private static boolean rechaza(Runnable accion) {
        try { accion.run(); return false; }
        catch (IllegalArgumentException esperado) { return true; }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalStateException("Falló la comprobación: " + mensaje);
    }
}
