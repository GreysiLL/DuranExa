package pe.upeu.andinasalud.presentation.detalle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.citas.nombreEstado
import pe.upeu.andinasalud.presentation.components.*
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
@Composable fun DetalleCitaScreen(estado: DetalleUiState,reintentar:()->Unit,cancelar:()->Unit,abrir:()->Unit,cerrar:()->Unit,cambiar:(String,String)->Unit,reprogramar:()->Unit) {
 var confirmar by remember { mutableStateOf(false) }
 EstadoCarga(estado.cargando,if(estado.cita==null)estado.error else null,reintentar) {
 val cita=estado.cita
 if(cita==null)Text("No se encontró la cita.",Modifier.padding(24.dp)) else
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
 item { Text(cita.medico.especialidad,style=MaterialTheme.typography.headlineSmall) }
 item { Text("Médico: ${cita.medico.nombre}");Text("Sede: ${cita.sede.nombre}");Text("Fecha: ${cita.fechaHora.date}");Text("Hora: ${cita.fechaHora.time}") }
 item { ModalidadCita(cita.modalidad) }
 item { Text("Estado: ${nombreEstado(cita.estado)}");Text("Motivo: ${cita.motivo}") }
 item { Text(when(val e=cita.estado){is EstadoCita.Atendida->"Indicaciones: ${e.indicaciones}";is EstadoCita.Cancelada->"Cancelación: ${e.motivo}";is EstadoCita.Programada->if(cita.modalidad==ModalidadAtencion.PRESENCIAL) "Indicaciones: llega 15 minutos antes y presenta tu documento." else "Indicaciones: prepara tu conexión y un lugar tranquilo para la teleconsulta."}) }
 item { if(estado.mensaje!=null)Text(estado.mensaje);if(estado.error!=null)Text(estado.error,color=MaterialTheme.colorScheme.error) }
 item { Button(onClick={confirmar=true},enabled=estado.puedeCancelar && !estado.cancelando && !estado.guardando && !estado.editando){Text(if(estado.cancelando)"Cancelando…" else "Cancelar cita")}
 Text("Solo citas Programadas con más de 24 horas de anticipación.",style=MaterialTheme.typography.bodySmall) }
 item {
   if(estado.puedeReprogramar && !estado.editando)OutlinedButton(onClick=abrir,enabled=!estado.cancelando){Text("Reprogramar cita")}
 }
 if(estado.editando) {
   item { Text("Nuevo horario",style=MaterialTheme.typography.titleMedium) }
   item { Campo("Nueva fecha (AAAA-MM-DD)",estado.fecha,estado.errores["fecha"],!estado.guardando){cambiar("fecha",it)} }
   item { Campo("Nueva hora (HH:MM)",estado.hora,estado.errores["hora"],!estado.guardando){cambiar("hora",it)} }
   item {
     estado.errores["general"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
     Button(onClick=reprogramar,enabled=!estado.guardando){Text(if(estado.guardando)"Guardando…" else "Guardar nuevo horario")}
     TextButton(onClick=cerrar,enabled=!estado.guardando){Text("Descartar cambios")}
   }
 }
 if(cita.cambiosHorario.isNotEmpty()) {
   item { Text("Historial de reprogramaciones",style=MaterialTheme.typography.titleMedium) }
   cita.cambiosHorario.forEach { cambio -> item {
     Text("Antes: ${cambio.anterior.date} · ${cambio.anterior.time}")
     Text("Ahora: ${cambio.nueva.date} · ${cambio.nueva.time}")
   } }
 }
 } }
 if(confirmar)AlertDialog(onDismissRequest={confirmar=false},title={Text("¿Cancelar esta cita?")},text={Text("La cita pasará al estado Cancelada.")},confirmButton={TextButton(onClick={confirmar=false;cancelar()}){Text("Sí, cancelar")}},dismissButton={TextButton(onClick={confirmar=false}){Text("Volver")}})
}
