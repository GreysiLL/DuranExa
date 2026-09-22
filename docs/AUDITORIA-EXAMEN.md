# Auditoría del examen AndinaSalud

Revisión: 22/09/2026. Fuente: examen de 13 páginas, secciones 3 a 9. Adaptaciones comunicadas por la estudiante: entrega individual, solo Android y las cuatro solicitudes SC-A a SC-D. Estas adaptaciones deben acompañar la evaluación; no se presenta iOS como probado.

## Resultado reproducible actual

Comando: `./gradlew.bat :shared:testAndroidHostTest :androidApp:assembleDebug`.

Resultado: BUILD SUCCESSFUL; 53 pruebas, 0 fallos, 0 errores, 0 omitidas. XML reales en RESULTADOS-FINAL y salida en VERIFICACION-FINAL.log. Desglose: 26 reglas, 16 Parte II, 9 estados de pantalla y 2 de plantilla. Las comprobaciones manuales previas y sus capturas se conservan por separado; no se afirma haber repetido todos los recorridos manuales en esta segunda auditoría.

## Funciones y reglas

Rutas relativas a `shared/src/commonMain/kotlin/pe/upeu/andinasalud/`.

| Requisito | Implementación revisada | Evidencia |
|---|---|---|
| RF-01 Inicio | presentation/inicio/InicioScreen.kt, CitasViewModel | Captura final 01, próxima Programada y accesos |
| RF-02 Lista y estados | presentation/citas, ObtenerCitasUseCase | Pruebas de orden y filtros, captura final 06 |
| RF-03 Detalle y cancelación | presentation/detalle, CancelarCitaUseCase | Pruebas de cancelación y captura final 07 |
| RF-04 Solicitud | presentation/solicitud, SolicitarCitaUseCase | Errores por campo, registro y doble pulsación |
| RF-05 Búsqueda | filtrarCitas, CitasViewModel | Pruebas de mayúsculas y tildes; registro manual |
| RF-06 Perfil y tema | presentation/perfil, App.kt, theme | Capturas finales 02 a 05 |
| RF-07 Navegación | presentation/navigation/AppNavHost.kt, AtrasSistema Android | Recorridos manuales previos de retorno y seis pantallas |
| RF-08 Estados | EstadoCarga y UiState; repositorio con delay(800) | 9 pruebas de estados; banco visual debug identificado en informe previo |
| RN-01 Fecha/hora | domain/usecase/ReglasCita.kt | Instante anterior rechazado; igualdad permitida |
| RN-02 Máximo tres | ReglasCita.contarProgramadas y puedeSolicitar | Límite, concurrencia y contador reactivo |
| RN-03 Cancelar >24 horas | ReglasCita.puedeCancelar | Estado y frontera exacta de 24 horas |
| RN-04 Motivo | ReglasCita.validarSolicitud | Límites de 10 y 200 caracteres, texto recortado |
| RN-05 Duplicados | ReglasCita.validarSolicitud | Paciente, estado, fecha y hora; concurrencia |

## Parte II

| Cambio | Ubicación de la lógica | Verificación |
|---|---|---|
| SC-A Hoy | CitasViewModel.visibles y filtrarCitas | Se combina con estado y búsqueda; fecha local del reloj |
| SC-B Contador y bloqueo | ReglasCita; valor expuesto por CitasViewModel | Cuenta global independiente de filtros; actualiza al cancelar/registrar |
| SC-C Modalidad | Modelo ModalidadAtencion, formulario, repositorio, ModalidadCita | Conserva Presencial/Teleconsulta y usa iconos distintos en las pantallas |
| SC-D Reprogramar | ReprogramarCitaUseCase y DetalleCitaViewModel | Reutiliza validarSolicitud, excluye la propia cita y guarda historial |

Las 16 pruebas de ParteDosTest y las siete comprobaciones de pruebas-parte-II-manuales.json están aprobadas. Las imágenes de CAPTURAS-PARTE-II documentan esos recorridos. El nombre sc-abcd-duran refleja que se solicitaron los cuatro cambios; no acredita cuatro ramas históricas independientes.

## Arquitectura y datos

Entidades y EstadoCita sealed class en domain/model; interfaz CitaRepository en domain/repository; implementación en memoria en data/repository. Los casos de uso dependen del contrato, no de la fuente simulada. ViewModels con MutableStateFlow privado y StateFlow público. Koin conecta las dependencias y comparte el bloqueo de operaciones. Compose usa callbacks, LazyColumn, Scaffold y tema propio. No se añadieron dependencias de API ni base de datos.

Semilla comprobada: un paciente fijo, cuatro sedes, cinco especialidades, diez médicos y seis citas (tres Programadas futuras, dos Atendidas y una Cancelada). Las fechas futuras son relativas al día de inicio. Los datos se reinician al finalizar el proceso, como corresponde a una fuente en memoria.

## Corrección de esta revisión

DetalleCitaViewModel ahora muestra un aviso útil si la excepción de carga o cancelación no tiene mensaje o tiene un mensaje vacío. Dos pruebas verifican recuperación, conservación de la cita y reintento exitoso. Se mantiene la propagación de CancellationException, sin convertir la cancelación de una corrutina en un error de interfaz.

## Entrega y límites de la calificación

- Código, pruebas y documentación finales están preparados localmente. Ejecutar finalizar-entrega.ps1 desde la sesión propietaria para publicar sin forzar el historial y crear v1.0-unidad1 sobre main. El script verifica ambos identificadores remotos.
- No hay evidencia de pull requests revisados por otra persona. Trabajo individual no significa que las revisiones originales del enunciado hayan ocurrido.
- La Parte II se concentró en un commit funcional. Los commits posteriores no cumplen retrospectivamente los tres commits separados durante los 120 minutos. El enunciado contempla descuento por ello.
- Evidencia-Git-AndinaSalud.pdf incorpora ahora la captura real de la terminal con shortlog y el gráfico de ramas. La entrega original y v1.0-unidad1 ya se publicaron; el complemento documental conserva la etiqueta.
- La defensa vale dos puntos y requiere mostrar y explicar el código. No se puede garantizar 20/20 a partir de la compilación o de estas pruebas.

PDF principal: Pruebas-Funcionales-AndinaSalud-Capturas.pdf, dos imágenes por hoja. PDF adicional: Evidencia-Git-AndinaSalud.pdf. Los informes de 35 y 51 pruebas son registros históricos; los 53 resultados actuales están en RESULTADOS-FINAL.
