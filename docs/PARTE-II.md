# Parte II - SC-A, SC-B, SC-C y SC-D

Se implementan las cuatro opciones por la indicación del docente comunicada por la estudiante. Trabajo individual, sin atribuir contribuciones a otra persona.

## Cómo demostrar los cambios

1. Inicio: la semilla contiene tres citas Programadas. La barra inferior muestra 3 y Solicitar cita está deshabilitado.
2. Citas: activar Hoy. Inicialmente no hay citas hoy. Se combina con Programada/Atendida/Cancelada y con búsqueda sin tildes.
3. Abrir una cita Programada y pulsar Reprogramar cita. Cambiar fecha y hora; Guardar nuevo horario. Se conserva el ID y aparece el historial Antes/Ahora. No se crea otra cita ni se consume otra plaza.
4. Para demostrar Hoy con resultados, reprogramar una cita para una hora futura del día actual. Volver a Citas y activar Hoy.
5. Cancelar otra cita con más de 24 horas: el contador baja a 2 y se habilita Solicitar cita.
6. Solicitar una cita seleccionando Teleconsulta. Al guardar, comprobar modalidad e icono en detalle y listado, y contador 3.

## Dónde está cada cambio

- SC-A: CitasUiState.soloHoy, CitasViewModel.cambiarHoy/visibles, filtrarCitas. La fecha del día se obtiene del Reloj y su zona local. El composable únicamente muestra el chip y envía la pulsación.
- SC-B: ReglasCita.contarProgramadas/puedeSolicitar. CitasViewModel publica programadas y puedeSolicitar. Inicio, lista, formulario y barra de navegación consumen ese estado. El contador no depende de los filtros y se actualiza cuando cambia el repositorio.
- SC-C: ModalidadAtencion en domain/model y propiedad modalidad de Cita. La semilla contiene ambas modalidades. El formulario transmite el enum al caso de uso; el repositorio conserva el modelo. ModalidadCita dibuja persona o cámara junto al nombre de la modalidad.
- SC-D: ReprogramarCitaUseCase y CambioHorario. El detalle permite editar fecha/hora y muestra todas las reprogramaciones. La hora de registro también queda en el modelo.

## Reutilización de validaciones

Solicitud y reprogramación comparten leerFechaHora y ReglasCita.validarSolicitud. Al reprogramar se excluye la propia cita del conjunto de comparación, pues ya ocupa una plaza y no debe duplicarse consigo misma. Se exige estado Programada, nuevo horario válido y distinto, sin fecha pasada ni choque con otra Programada. La restricción de más de 24 horas pertenece a cancelar; el enunciado SC-D no la exige para reprogramar.

Un Mutex compartido coordina solicitar, cancelar y reprogramar. Dos operaciones simultáneas no pueden reservar el mismo horario. Si una validación falla, no se modifica el repositorio ni se añade historial.

## Verificación

Gradle: :shared:testAndroidHostTest y :androidApp:assembleDebug.
51 pruebas aprobadas: 26 de reglas, 7 de estados de pantalla, 16 de ParteDosTest y 2 de plantilla. Registro: PRUEBAS-PARTE-II.log. ParteDosTest comprueba filtros combinados y zona local, contador, modalidad, validaciones, concurrencia e historial de reprogramación.
Las capturas del recorrido Android se guardan en CAPTURAS-PARTE-II y las comprobaciones en pruebas-parte-II-manuales.json. No representan pruebas de iOS.

## Git y entrega

Los cambios están en archivos locales. Git negó la creación de sc-abcd-duran por permisos del directorio .git. Ejecutar guardar-parte-II.ps1 desde la terminal de AndinaSalud crea la rama y registra un commit real del estado actual. No publica ni crea commits retrospectivos. Ese commit no sustituye el requisito del examen de commits distribuidos durante el desarrollo. iOS, publicación, revisión e integración final siguen pendientes.
