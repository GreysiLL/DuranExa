# AndinaSalud

Aplicación Kotlin Multiplatform de citas médicas con datos en memoria, Clean + MVVM, Compose y Koin. Paquete: pe.upeu.andinasalud.

## Ejecutar

Abrir en Android Studio, sincronizar Gradle y ejecutar androidApp. Usar JDK 21 y SDK 37 (mínimo 24), según la plantilla generada.

```powershell
.\gradlew.bat :shared:testAndroidHostTest :androidApp:assembleDebug
```

Para iOS: en macOS con Xcode abrir iosApp/iosApp.xcodeproj y ejecutar en un simulador. Sus destinos están configurados; su ejecución no se ha verificado en Windows.

## Funciones

Inicio con próxima cita; listado ordenado, búsqueda sin tildes ni mayúsculas y filtros; detalle con cancelación confirmada; solicitud con errores por campo; perfil; ajustes de tema claro/oscuro. Perfil y Ajustes se separaron para obtener seis pantallas: confirmar esta interpretación con el docente. La barra inferior mantiene Inicio, Citas y Perfil.

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

La estudiante indicó modalidad individual aunque el PDF describe una pareja. No se simulan aportes ni revisiones de terceros. La rama feature/base-andinasalud-duran nace de develop. main conserva únicamente la plantilla inicial; las funcionalidades se desarrollan en la rama de trabajo.

Los pull requests, revisiones y requisitos colaborativos deben acordarse con el docente para la modalidad individual. No se ha publicado el proyecto ni se ha creado la etiqueta final. Las solicitudes SC-A a SC-D corresponden al bloque individual del examen y no se han implementado anticipadamente.

## Verificar y entregar

ReglasCitaTest prueba RN-01 a RN-05, fronteras, filtros, datos semilla y concurrencia. Revisar los resultados reales de Gradle antes de afirmar que están aprobadas.

Pendientes de entrega: ejecutar y revisar Android e iOS; capturar las seis pantallas por plataforma; incorporar capturas y evidencia de Git al PDF; completar la solicitud asignada; realizar las fusiones revisadas que correspondan y etiquetar v1.0-unidad1.
