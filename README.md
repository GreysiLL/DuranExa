# AndinaSalud

Aplicación Kotlin Multiplatform de citas médicas con datos en memoria, Clean + MVVM, Compose y Koin. Paquete: pe.upeu.andinasalud.

## Ejecutar

Abrir en Android Studio, sincronizar Gradle y ejecutar androidApp. Usar JDK 21 y SDK 37 (mínimo 24), según la plantilla generada.

```powershell
.\gradlew.bat :shared:testAndroidHostTest :androidApp:assembleDebug
```

Alcance de entrega: Android, según la autorización del profesor comunicada por la estudiante el 22/09/2026. iOS mantiene sus destinos configurados, pero no se ha ejecutado ni se presenta como verificado.

## Funciones

Inicio con próxima cita; listado ordenado, búsqueda sin tildes ni mayúsculas y filtros; detalle con cancelación confirmada; solicitud con errores por campo; perfil; ajustes de tema claro/oscuro. Las seis pantallas son Inicio, Citas, Detalle, Solicitud, Perfil y Ajustes. La barra inferior mantiene Inicio, Citas y Perfil.

## Organización

En shared/src/commonMain/kotlin/pe/upeu/andinasalud:

- domain/model: entidades y EstadoCita como sealed class.
- domain/repository: contrato CitaRepository.
- domain/usecase: obtener, solicitar, cancelar y las cinco reglas en ReglasCita.
- data/local: un paciente, cuatro sedes, cinco especialidades, diez médicos y seis citas.
- data/repository: lista en memoria, retardo de 800 ms y notificación de cambios.
- presentation: ViewModels con StateFlow, UiState, pantallas y componentes reutilizables.
- di: módulos de Koin, iniciados en Android y en iOS.

Recorrido: Screen → ViewModel → caso de uso → contrato del repositorio → implementación simulada. El resultado actualiza StateFlow y Compose vuelve a dibujar. Las pantallas no acceden a data.

El reloj se inyecta para comprobar reglas temporales. Un Mutex compartido serializa solicitudes y cancelaciones para evitar carreras en el límite y duplicados. El repositorio protege también su lista. No se usa red ni persistencia.

El formulario asigna el primer médico de la especialidad que atiende en la sede: el enunciado no solicita selector de médico. Las fechas semilla son relativas al inicio. Hay tres citas Programadas iniciales: cancelar una válida libera espacio para solicitar otra. Los datos se pierden al terminar el proceso.

Para una API futura se crea una implementación del contrato en data, se mapean sus respuestas a modelos y se cambia la definición de Koin. Esta versión no incorpora ninguna librería de red ni base de datos.

## Trabajo individual y ramas

La estudiante indicó modalidad individual aunque el PDF describe una pareja. No se simulan aportes ni revisiones de terceros. La rama feature/base-andinasalud-duran nace de develop. Las funcionalidades se desarrollaron en feature/base-andinasalud-duran y sc-abcd-duran. El script de publicación integra esas ramas en develop y después en main mediante commits de fusión.

Los pull requests, revisiones y requisitos colaborativos deben acordarse con el docente para la modalidad individual. El repositorio de entrega es https://github.com/GreysiLL/DuranExa. El cierre con la etiqueta v1.0-unidad1 se realiza mediante finalizar-entrega.ps1; consultar las etiquetas publicadas para verificar su ejecución. Tras la revisión, la estudiante comunicó que el docente solicitó realizar las cuatro opciones SC-A a SC-D. Están implementadas; ver docs/PARTE-II.md. La creación de sc-abcd-duran desde esta sesión fue bloqueada por permisos de Git en Windows. El script guardar-parte-II.ps1 permite crear esa rama y guardar los cambios desde la terminal del propietario.

## Verificar y entregar

ReglasCitaTest prueba RN-01 a RN-05, fronteras, filtros, datos semilla y concurrencia. Revisar los resultados reales de Gradle antes de afirmar que están aprobadas.

Android se compiló y verificó. La entrega final y sus límites se describen en docs/ENTREGA-FINAL.md. La defensa y la evaluación de los requisitos históricos de Git corresponden al profesor.

## Evidencias de pruebas Android

Ver [informe de pruebas](docs/Informe-Pruebas-AndinaSalud.pdf) y [verificacion](docs/VERIFICACION.md). El informe anterior documenta las 35 pruebas de la versión base. La revisión actual pasa 53 pruebas (docs/VERIFICACION-FINAL.log y docs/RESULTADOS-FINAL); el registro anterior de Parte II contiene 51 pruebas. iOS está fuera del alcance autorizado comunicado para esta entrega. El banco visual de estados solo existe en debug.

## Cambios de la Parte II

- SC-A: chip Hoy combinado con estado y búsqueda, resuelto por el ViewModel usando la fecha local.
- SC-B: contador global de Programadas y botones de solicitud deshabilitados al llegar al límite definido en el dominio.
- SC-C: Presencial o Teleconsulta, con iconos distintos en formulario, tarjetas y detalle.
- SC-D: reprogramación desde el detalle; conserva la cita, reutiliza las validaciones y guarda el historial de horarios.

Ver [explicación y pruebas](docs/PARTE-II.md). Las capturas y PDF de la versión base son evidencia histórica, anterior a estos cambios.

## Repositorio de entrega

Publicación con `subir-andinasalud.ps1`: conserva main, develop, feature/base-andinasalud-duran y sc-abcd-duran, sin forzar cambios remotos. Las fusiones locales no equivalen a revisiones por pull request.

[PDF actualizado de pruebas funcionales](docs/Pruebas-Funcionales-AndinaSalud-Capturas.pdf): dos capturas por hoja y explicación breve. Las imágenes originales de la Parte II están en [CAPTURAS-PARTE-II](docs/CAPTURAS-PARTE-II) y [PARTE-II.md](docs/PARTE-II.md).

## Entrega final Android

Ver [alcance y comprobaciones](docs/ENTREGA-FINAL.md), [capturas funcionales](docs/Pruebas-Funcionales-AndinaSalud-Capturas.pdf) y [evidencia Git](docs/Evidencia-Git-AndinaSalud.pdf). Se conservan los informes anteriores como registros históricos.

La entrega es individual; no se acredita revisión cruzada. Los commits nuevos no representan trabajo registrado retrospectivamente durante el examen.

La [auditoría por requisito](docs/AUDITORIA-EXAMEN.md) identifica lo comprobado y los pendientes de entrega.
