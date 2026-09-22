# Guía para defender AndinaSalud

Abre Android Studio. Pulsa dos veces Mayús y escribe el nombre del archivo que se indica. Dentro de shared/commonMain/kotlin/pe.upeu.andinasalud están las capas. Lee y prueba cada explicación; esta guía no sustituye tu defensa.

## 1. ¿Dónde vive RN-02?
Abre ReglasCita.kt. Muestra MAX_PROGRAMADAS, contarProgramadas y puedeSolicitar. «El dominio decide que un paciente tenga como máximo tres citas Programadas. La pantalla recibe si puede solicitar; así la regla sigue funcionando aunque cambie el diseño». Muestra CitasViewModel.kt y el contador que expone. En la app: con tres citas se bloquea Solicitar; cancela una permitida y observa que baja a dos.

## 2. ¿Cómo conectar una API?
Es una propuesta futura, no una función ya implementada. Crearías CitaApi.kt (peticiones), los DTO y sus mapeos, y CitaRepositoryRemoto.kt que implemente CitaRepository. Modificarías AppModule.kt para inyectar esa implementación y shared/build.gradle.kts para agregar el cliente y motores necesarios por plataforma. Android necesitaría permiso INTERNET en su manifest. El contrato exige también notificar cambios: la implementación nueva debe mantener ese comportamiento. Pantallas y casos de uso conservarían el mismo contrato. Muestra CitaRepository.kt y la línea single<CitaRepository> en AppModule.kt.

## 3. ¿Por qué sealed class?
Abre EstadoCita.kt. «No guardo solo un nombre: cada estado trae información diferente. Programada lleva recordatorioActivo; Atendida lleva indicaciones; Cancelada lleva motivo y canceladaPorPaciente». Una cadena permite errores como escribir mal Cancelada. Un enum sirve para opciones fijas, pero no representa tan directamente datos diferentes por cada cita y estado. Sealed limita las variantes y permite que when compruebe que atendemos todos los casos. Muestra el when de DetalleCitaScreen.kt y abre una cita cancelada para ver su motivo.

## 4. Recorrido del dato hasta la pantalla
Abre en orden CitasSimuladas.kt → CitaRepositoryFake.kt → ObtenerCitasUseCase.kt → CitasViewModel.kt → AppNavHost.kt → CitasScreen.kt → Componentes.kt. «La fuente crea las citas. El repositorio las entrega. El caso de uso obtiene y ordena. El ViewModel actualiza UiState dentro de StateFlow. La navegación observa ese estado y lo pasa a la pantalla. La tarjeta dibuja médico, sede y horario». AppModule.kt conecta estas piezas; la pantalla no consulta CitasSimuladas.

## 5. UiState frente a modelo
Abre Cita.kt y CitasUiState.kt. «Cita representa una cita médica: médico, fecha, estado y modalidad. UiState representa cómo está la pantalla: cargando, error, búsqueda, filtro y datos visibles. Un error de carga no es una propiedad de una cita».

## 6. Corrutina y destrucción
Abre CitaRepositoryFake.kt y señala delay(800). Luego CitasViewModel.kt: viewModelScope.launch. «delay suspende la corrutina; no bloquea el teléfono. Cuando se elimina el ViewModel se cancela su scope. Por eso propago CancellationException». Salir de un composable no siempre destruye el ViewModel: depende de su propietario. No digas que cada cambio de pestaña lo destruye. EstadosPantallaTest comprueba la cancelación cuando se limpia ViewModelStore.

## 7. StateFlow
En CitasViewModel.kt muestra mutable privado y estado=mutable.asStateFlow(). «La pantalla observa cambios, pero no modifica directamente el estado. Llama a buscar o filtrar y el ViewModel lo actualiza. Esto concentra las decisiones y mantiene coherente lo que se dibuja».

## 8. Componente reutilizable
En Componentes.kt muestra TarjetaCita(cita, abrir). «Recibe los datos y una acción para abrir. Dibuja la tarjeta. No sabe de repositorios, API, Koin ni reglas de cancelación». También Campo recibe etiqueta, valor, error y callback; no decide si una fecha es válida.

## 9. Tema
Abre App.kt, AndinaSaludTheme.kt y PerfilScreen.kt (AjustesScreen). «El interruptor cambia oscuro. App aplica el tema alrededor de toda la navegación, por eso cambia toda la app. rememberSaveable conserva la elección durante la recreación de la pantalla; no es almacenamiento permanente de preferencias». Demuestra Perfil → Ajustes → modo oscuro → Inicio.

## 10. Agregar especialidades
Abre CitasSimuladas.kt. «En esta fuente modificaría un archivo de producción: especialidades y los nombres de los médicos. Cada especialidad necesita dos médicos; no basta añadir el nombre porque se indexa la lista de nombres de dos en dos». Actualiza también la prueba que comprueba la cantidad del catálogo. Las pantallas leen el catálogo, por eso no tendrías que añadir botones manualmente en cada pantalla.

## 11. Ramas y aportes
Muestra el gráfico del PDF o ejecuta evidencia.ps1. Las ramas reales incluyen feature/base-andinasalud-duran, sc-abcd-duran y fix/entrega-final-duran. «El trabajo fue individual según la modalidad que comuniqué. La base implementa citas y reglas; sc-abcd-duran contiene los cuatro cambios». Si no hubo conflicto de fusión, dilo: un problema de permisos Git no es un conflicto de merge. No afirmes revisiones de un compañero que no ocurrieron.

## Recorrido corto para demostrar la Parte II
1. Inicio con tres Programadas: contador tres y Solicitar deshabilitado.
2. Citas: combina Hoy con un estado; explica que el filtro está en el ViewModel.
3. Abre una Programada futura: reprograma y muestra horario anterior y nuevo.
4. Cancela una Programada con más de 24 horas: contador dos y solicitud habilitada.
5. Solicita Teleconsulta con motivo válido: muestra el icono y modalidad en el detalle.
6. En Ajustes cambia el tema y vuelve a Inicio.

Ensaya con el emulador. El historial de trabajo y la defensa no se pueden sustituir por esta guía ni por pruebas automatizadas.
