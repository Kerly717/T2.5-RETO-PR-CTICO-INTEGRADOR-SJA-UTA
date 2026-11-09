package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.Scanner;
import java.util.function.Supplier;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;
import negocio.NodoUnidad;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static ArbolOrganizacional arbol = nuevoArbol();

    private static ArbolOrganizacional nuevoArbol() {
        ArbolOrganizacional nuevo = new ArbolOrganizacional(DatosPrueba.universidad());
        DatosPrueba.cargar(nuevo);
        return nuevo;
    }

    public static void main(String[] args) {
        System.out.println("=== SJA-UTA: Sistema Jerárquico Académico ===");
        System.out.println("Datos iniciales cargados");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            procesar(opcion);
        } while (opcion != 0);
        System.out.println("Hasta luego.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("--- Organización (árbol general) ---");
        System.out.println(" 1. Mostrar jerarquía");
        System.out.println(" 2. Agregar unidad");
        System.out.println(" 3. Buscar unidad");
        System.out.println(" 4. Altura y hojas del árbol");
        System.out.println(" 5. Grado y nivel de una unidad");
        System.out.println(" 6. Eliminar unidad");
        System.out.println(" 7. Reiniciar datos iniciales");
        System.out.println(" 8. Ejecutar reto integrador");
        System.out.println(" 9. Transformar a árbol binario");
        System.out.println("--- Estudiantes de un paralelo (ABB) ---");
        System.out.println("10. Insertar estudiante");
        System.out.println("11. Buscar estudiante");
        System.out.println("12. Recorrer (pre / in / posorden)");
        System.out.println("13. Modificar estudiante");
        System.out.println("14. Eliminar estudiante");
        System.out.println("15. Estadísticas del paralelo");
        System.out.println("16. Traza de recorrido");
        System.out.println(" 0. Salir");
    }

    private static void procesar(int opcion) {
        switch (opcion) {
            case 1 -> ejecutar("Jerarquía", arbol::mostrarJerarquia);
            case 2 -> ejecutar("Agregar unidad", () -> {
                String padre = leerTexto("Código del padre: ");
                String codigo = leerTexto("Código: ");
                String nombre = leerTexto("Nombre: ");
                TipoUnidad tipo = TipoUnidad.valueOf(leerTexto("Tipo " + java.util.Arrays.toString(TipoUnidad.values()) + ": ").toUpperCase());
                return arbol.agregar(padre, new UnidadAcademica(codigo, nombre, tipo)) ? "Unidad agregada" : "No se pudo agregar";
            });
            case 3 -> ejecutar("Buscar unidad", () -> {
                NodoUnidad n = arbol.buscar(leerTexto("Código: "));
                return n == null ? "No existe" : n.getDato().toString();
            });
            case 4 -> ejecutar("Altura y hojas", () -> "Altura: " + arbol.altura() + " | Hojas: " + arbol.contarHojas());
            case 5 -> ejecutar("Grado y nivel", () -> {
                String codigo = leerTexto("Código: ");
                return "Grado: " + arbol.grado(codigo) + " | Nivel: " + arbol.nivel(codigo);
            });
            case 6 -> ejecutar("Eliminar unidad", () -> arbol.eliminar(leerTexto("Código: ")) ? "Unidad eliminada" : "No se pudo eliminar");
            case 7 -> ejecutar("Reiniciar", () -> {
                arbol = nuevoArbol();
                return "Datos iniciales reconstruidos: 9 en EB-N1-A, 0 en EB-N1-B y sin SW";
            });
            case 8 -> ejecutar("Reto integrador", () -> RetoIntegrador.ejecutar(arbol));
            case 9 -> ejecutar("Árbol binario", arbol::transformarABinario);
            case 10 -> ejecutar("Insertar estudiante", () -> {
                ArbolEstudiantes abb = paralelo();
                Estudiante e = new Estudiante(leerEntero("Matrícula: "), leerTexto("Cédula: "), leerTexto("Nombres: "), leerDecimal("Promedio: "));
                return abb.insertar(e) ? "Estudiante insertado" : "Matrícula repetida";
            });
            case 11 -> ejecutar("Buscar estudiante", () -> {
                Estudiante e = paralelo().buscar(leerEntero("Matrícula: "));
                return e == null ? "No existe" : e.toString();
            });
            case 12 -> ejecutar("Recorrido", () -> {
                ArbolEstudiantes abb = paralelo();
                TipoRecorrido tipo = TipoRecorrido.valueOf(leerTexto("Tipo " + java.util.Arrays.toString(TipoRecorrido.values()) + ": ").toUpperCase());
                StringBuilder sb = new StringBuilder();
                abb.recorrer(tipo).forEach(e -> sb.append(e).append(System.lineSeparator()));
                return sb.toString();
            });
            case 13 -> ejecutar("Modificar estudiante", () -> paralelo().modificar(leerEntero("Matrícula: "), leerTexto("Nueva cédula: "),
                    leerTexto("Nuevos nombres: "), leerDecimal("Nuevo promedio: ")) ? "Estudiante modificado" : "No existe");
            case 14 -> ejecutar("Eliminar estudiante", () -> paralelo().eliminar(leerEntero("Matrícula: ")) ? "Estudiante eliminado" : "No existe");
            case 15 -> ejecutar("Estadísticas", () -> {
                ArbolEstudiantes abb = paralelo();
                return "Total: " + abb.contar() + " | Altura: " + abb.altura() + " | Menor: "
                        + (abb.menor() == null ? "—" : abb.menor().getMatricula())
                        + " | Mayor: " + (abb.mayor() == null ? "—" : abb.mayor().getMatricula())
                        + String.format(java.util.Locale.ROOT, " | Promedio general: %.2f", abb.promedioGeneral())
                        + " | Aprobados (>= 8.00): " + abb.contarAprobados(8.00);
            });
            case 16 -> ejecutar("Traza de recorrido", () -> {
                ArbolEstudiantes abb = paralelo();
                TipoRecorrido tipo = TipoRecorrido.valueOf(leerTexto("Tipo " + java.util.Arrays.toString(TipoRecorrido.values()) + ": ").toUpperCase());
                return System.lineSeparator() + abb.trazaRecorrido(tipo);
            });
            case 0 -> {
            }
            default -> System.out.println("Opción no válida");
        }
    }

    private static ArbolEstudiantes paralelo() {
        NodoUnidad nodo = arbol.buscar(leerTexto("Código del paralelo (ej. SW-N3-B): "));
        if (nodo == null || nodo.getEstudiantes() == null) {
            throw new IllegalArgumentException("Ese código no es un paralelo");
        }
        return nodo.getEstudiantes();
    }

    private static void ejecutar(String titulo, Supplier<String> accion) {
        try {
            System.out.println("[" + titulo + "] " + accion.get());
        } catch (UnsupportedOperationException e) {
            System.out.println("[" + titulo + "] [PENDIENTE] " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[" + titulo + "] [ERROR] " + e.getMessage());
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número");
            }
        }
    }
}
