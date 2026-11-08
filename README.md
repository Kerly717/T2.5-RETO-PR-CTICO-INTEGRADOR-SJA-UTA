# SJA UTA reto práctico integrador

Proyecto Java de consola basado en `RETO_CLASS.zip`, ajustado a las condiciones del enunciado de APE 3. Conserva los paquetes `enums`, `modelo`, `negocio` y `app`, y las clases y firmas existentes dentro de `src`.

## Cómo compilar y ejecutar

Desde esta carpeta, con JDK 17 o superior y PowerShell:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
$fuentes = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d build $fuentes
java -cp build app.Main
```

La opción **8** ejecuta el reto completo. Si se selecciona de nuevo, verifica el estado final y no duplica unidades ni estudiantes. La opción **7** reconstruye la instancia inicial; luego puede ejecutarse de nuevo la opción 8. La opción **9** muestra la representación hijo izquierdo / hermano derecho.

## Decisiones de implementación

- La carga de `DatosPrueba` se adaptó al enunciado: `UTA → FCHE → EB → EB-N1`, con `EB-N1-A` y `EB-N1-B`; las nueve matrículas se insertan en el orden indicado y todas empiezan con promedio 8.00. El ZIP original incluía otras facultades y ubicaba `SW` en `FISEI`, situación incompatible con la misión 1 y con el reinicio solicitado.
- La ruta se obtiene mediante búsqueda recursiva y retroceso sobre el árbol general. El nivel de la raíz es 0; la altura se mide en aristas. Los códigos inexistentes retornan `No existe` para ruta, `-1` para nivel y grado.
- La eliminación de unidades exige que sean hojas; un paralelo con estudiantes no se elimina. La transformación binaria produce una representación textual de hijo izquierdo y hermano derecho sin modificar los nodos originales.
- El traslado valida ambos paralelos, la existencia del estudiante y la ausencia de duplicado. Primero inserta en destino, luego elimina de origen; si esta última operación falla, revierte la inserción. Se conserva la misma instancia de `Estudiante`.
- `Estudiante` rechaza matrícula no positiva y promedios fuera de 0 a 10, `NaN` e infinitos. `ArbolEstudiantes.modificar` valida todos los nuevos datos con un objeto temporal antes de cambiar el registro real.
- Las estadísticas del ABB usan recorridos propios: altura, cantidad, extremos, suma de promedios y conteo recursivo de aprobados. En un ABB vacío, menor y mayor son `null`, altura `-1` y promedio general `0.00`.

## Resultados de la ejecución

La salida real del menú está en `evidencias/ejecucion-menu.txt`. Incluye la ejecución de 8 dos veces, el reinicio con 7, la búsqueda de `SW` y las estadísticas restauradas de ambos paralelos. La salida de las pruebas está en `evidencias/pruebas.txt`.

Después de los cuatro traslados, `EB-N1-A` contiene `15, 20, 35, 50, 55` y `EB-N1-B` contiene `10, 30, 40, 45`. Sus promedios son 8.36 y 8.00. El recorrido inorden queda ascendente porque visita primero las matrículas menores del subárbol izquierdo, luego la raíz y finalmente las mayores del subárbol derecho.

## Pruebas automatizadas

```powershell
javac -encoding UTF-8 -cp build -d build tests/PruebasReto.java
java -cp build PruebasReto
```

Las pruebas comprueban el estado inicial, rutas, alturas, datos vacíos, casos de eliminación, resultados finales, repetición sin duplicados y reconstrucción exacta mediante un árbol nuevo, equivalente a la opción 7.
