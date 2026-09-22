package pe.upeu.andinasalud.presentation.solicitud
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.*
@Composable fun SolicitudScreen(s: SolicitudUiState,reintentar:()->Unit,cambiar:(String,String)->Unit,registrar:()->Unit,detalle:(Int)->Unit) {
 EstadoCarga(s.cargando,s.errorCarga,reintentar) {
 val c=s.catalogo
 if(c==null || c.especialidades.isEmpty() || c.sedes.isEmpty())Text("No hay opciones disponibles.") else
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
 item { Text("Solicitar cita",style=MaterialTheme.typography.headlineSmall);Text("El médico se asigna según especialidad y sede.") }
 item { Selector("Especialidad",s.especialidad,c.especialidades.map { it to it },s.errores["especialidad"],!s.guardando){cambiar("especialidad",it)} }
 item { Selector("Sede",s.sede,c.sedes.map { it.id to it.nombre },s.errores["sede"],!s.guardando){cambiar("sede",it)} }
 item { Campo("Fecha (AAAA-MM-DD)",s.fecha,s.errores["fecha"],!s.guardando){cambiar("fecha",it)} }
 item { Campo("Hora (HH:MM)",s.hora,s.errores["hora"],!s.guardando){cambiar("hora",it)} }
 item { Campo("Motivo (10 a 200 caracteres)",s.motivo,s.errores["motivo"],!s.guardando){cambiar("motivo",it)} }
 item { s.errores["general"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
  Button(onClick=registrar,enabled=!s.guardando && s.registrada==null,modifier=Modifier.fillMaxWidth()){Text(if(s.guardando)"Registrando…" else "Solicitar cita")}
  s.registrada?.let { id -> Text("Cita registrada correctamente.");OutlinedButton(onClick={detalle(id)}){Text("Ver cita")} }
 }
 } }
}
