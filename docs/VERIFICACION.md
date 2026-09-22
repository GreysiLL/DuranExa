# Actualización: Parte II

La versión actual incluye SC-A a SC-D y pasa 51 pruebas. Ver PARTE-II.md y PRUEBAS-PARTE-II.log. El registro que sigue corresponde a la verificación anterior de la aplicación base.

# Verificación Android del 22 de septiembre de 2026

- Rama: feature/base-andinasalud-duran.
- Compilación final: BUILD SUCCESSFUL.
- Tests automatizados: 35, sin fallos ni errores (33 específicos y 2 de la plantilla).
- Se comprobaron en el emulador: inicio, filtros, búsqueda, estados vacíos, validaciones, límite de citas, cancelación, registro, retorno, actualización de datos, tema y conservación del formulario al rotar.
- Corrección aplicada: contraste de los iconos de las barras del sistema Android al cambiar a modo oscuro.
- Las incidencias iniciales del selector de pruebas se conservan en pruebas-manuales.json junto con su repetición aprobada; no fueron fallos de las reglas de negocio.

## Evidencias

- Informe-Pruebas-AndinaSalud.pdf: informe ilustrado.
- CAPTURAS: 24 imágenes del emulador; 22 a 24 pertenecen al banco visual controlado de debug.
- PRUEBAS.log: ejecución final de tests y assembleDebug.
- RESULTADOS: XML reales de las pruebas.
- pruebas-manuales.json: comprobaciones y observaciones de la automatización.
- GIT-EVIDENCIA.txt: salida real del historial, autores y estado del repositorio.

EvidenciasActivity existe solo en src/debug y permite documentar estados sin provocar fallos en la fuente normal. Los tests del ViewModel verifican por separado la propagación del error del repositorio y la recuperación al reintentar. Al terminar quedó abierta MainActivity, la aplicación normal.

## Pendientes ajenos a esta verificación Android

No se ha ejecutado iOS. Faltan sus capturas reales y su verificación en macOS. La modalidad individual, revisión de ramas, publicación y etiqueta final deben resolverse antes de la entrega. Este informe no acredita aún el examen completo. Para guardar este avance local se preparó guardar-pruebas.ps1; no se realizaron commits ni publicaciones desde esta sesión.
