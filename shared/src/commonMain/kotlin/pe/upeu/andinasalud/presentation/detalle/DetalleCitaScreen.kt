package pe.upeu.andinasalud.presentation.detalle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.citas.nombreEstado
import pe.upeu.andinasalud.presentation.components.EstadoCarga
@Composable fun DetalleCitaScreen(estado: DetalleUiState,reintentar:()->Unit,cancelar:()->Unit) {
 var confirmar by remember { mutableStateOf(false) }
 EstadoCarga(estado.cargando,if(estado.cita==null)estado.error else null,reintentar) {
 val cita=estado.cita
 if(cita==null)Text("No se encontró la cita.",Modifier.padding(24.dp)) else
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
 item { Text(cita.medico.especialidad,style=MaterialTheme.typography.headlineSmall) }
 item { Text("Médico: ${cita.medico.nombre}");Text("Sede: ${cita.sede.nombre}");Text("Fecha: ${cita.fechaHora.date}");Text("Hora: ${cita.fechaHora.time}") }
 item { Text("Estado: ${nombreEstado(cita.estado)}");Text("Motivo: ${cita.motivo}") }
 item { Text(when(val e=cita.estado){is EstadoCita.Atendida->"Indicaciones: ${e.indicaciones}";is EstadoCita.Cancelada->"Cancelación: ${e.motivo}";is EstadoCita.Programada->"Indicaciones: llega 15 minutos antes y presenta tu documento."}) }
 item { if(estado.mensaje!=null)Text(estado.mensaje);if(estado.error!=null)Text(estado.error,color=MaterialTheme.colorScheme.error) }
 item { Button(onClick={confirmar=true},enabled=estado.puedeCancelar && !estado.cancelando){Text(if(estado.cancelando)"Cancelando…" else "Cancelar cita")}
 Text("Solo citas Programadas con más de 24 horas de anticipación.",style=MaterialTheme.typography.bodySmall) }
 } }
 if(confirmar)AlertDialog(onDismissRequest={confirmar=false},title={Text("¿Cancelar esta cita?")},text={Text("La cita pasará al estado Cancelada.")},confirmButton={TextButton(onClick={confirmar=false;cancelar()}){Text("Sí, cancelar")}},dismissButton={TextButton(onClick={confirmar=false}){Text("Volver")}})
}
