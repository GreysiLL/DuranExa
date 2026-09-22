# Entrega final - AndinaSalud

## Alcance autorizado

La estudiante informó el 22/09/2026: «El profesor autorizó solo Android». Se prepara esta entrega para Android; se mantiene la configuración KMP de iOS, sin afirmar que fue ejecutada. También indicó modalidad individual y que el docente le pidió implementar SC-A, SC-B, SC-C y SC-D.

## Evidencias actuales

- Pruebas-Funcionales-AndinaSalud-Capturas.pdf: 18 capturas en 9 páginas, dos por hoja. Incluye las seis pantallas Android, los temas y las cuatro solicitudes de cambio. Las primeras ocho capturas corresponden a la personalización actual del paciente; las de Parte II documentan los flujos previamente verificados.
- Evidencia-Git-AndinaSalud.pdf: captura real del gráfico de GitHub y del historial. Shortlog reproducido como texto, identificado como tal.
- GIT-FINAL: imágenes de GitHub y salidas reales de git log y git shortlog. Estado anterior al commit de cierre.
- VERIFICACION-FINAL.log y RESULTADOS-FINAL: compilación Android correcta y 53 pruebas aprobadas, sin fallos ni errores.
- CAPTURAS-FINAL: las seis pantallas Android con el nombre actualizado.
- CAPTURAS-PARTE-II: verificación de Hoy, límite, modalidad y reprogramación.

Los informes previos se conservan como evidencia histórica; el PDF principal para presentar es Pruebas-Funcionales-AndinaSalud-Capturas.pdf.

## Cierre de Git

finalizar-entrega.ps1 crea fix/entrega-final-duran desde develop, registra los cambios pendientes, integra mediante fusiones en develop y main, crea la etiqueta anotada v1.0-unidad1 y verifica que la etiqueta remota apunta al mismo commit que main. No fuerza cambios ni reemplaza etiquetas existentes en otra versión.

Hasta que se ejecute ese script desde la sesión de la estudiante, la etiqueta y los documentos nuevos no están publicados. La conexión del asistente recibió HTTP 403 Resource not accessible by integration.

## Requisitos que no se pueden acreditar retrospectivamente

- La Parte II funcional se registró principalmente en un commit; los commits del cierre no sustituyen los tres commits distribuidos durante el examen.
- Las fusiones existentes no equivalen a solicitudes de incorporación con revisión de un compañero. No se han inventado revisiones ni autores. El docente debe aplicar su adaptación a trabajo individual.
- La salida shortlog se incluye como texto; para cumplir literalmente el formato de captura de terminal, ejecutar git shortlog -sne --all y capturar su salida desde la sesión del usuario.
- La defensa técnica debe realizarla la estudiante mostrando y explicando el código.

## Segunda auditoría

Ver AUDITORIA-EXAMEN.md para la relación de requisitos, archivos y pruebas. Se corrigió el aviso de errores sin mensaje en el detalle y se añadieron dos pruebas de recuperación. Compilación y suite completas aprobadas después del cambio.
