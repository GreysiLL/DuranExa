package pe.upeu.andinasalud.presentation.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.presentation.citas.nombreEstado
@Composable fun EstadoCarga(cargando: Boolean,error: String?,reintentar: ()->Unit,contenido: @Composable ()->Unit) {
 when { cargando -> Column(Modifier.fillMaxWidth().padding(32.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) { CircularProgressIndicator();Text("Cargando…") }
 error!=null -> Column(Modifier.padding(24.dp)) { Text(error,color=MaterialTheme.colorScheme.error);Button(onClick=reintentar){Text("Reintentar")} }
 else -> contenido() }
}
@Composable fun TarjetaCita(cita: Cita,abrir: ()->Unit) {
 OutlinedCard(onClick=abrir,modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
   Text(cita.medico.especialidad,style=MaterialTheme.typography.titleMedium)
   Text(cita.medico.nombre);Text("${cita.sede.nombre} · ${cita.fechaHora.date} · ${cita.fechaHora.time}")
   ModalidadCita(cita.modalidad)
   Text(nombreEstado(cita.estado),color=MaterialTheme.colorScheme.primary)
 } }
}
@Composable fun Campo(etiqueta: String,valor: String,error: String?,habilitado: Boolean=true,cambiar: (String)->Unit) {
 OutlinedTextField(value=valor,onValueChange=cambiar,label={Text(etiqueta)},isError=error!=null,
  supportingText={if(error!=null)Text(error)},enabled=habilitado,modifier=Modifier.fillMaxWidth())
}
@Composable fun Selector(etiqueta: String,valor: String,opciones: List<Pair<String,String>>,error: String?,habilitado: Boolean=true,elegir: (String)->Unit) {
 var abierto by remember { mutableStateOf(false) }
 Column { Box {
  OutlinedButton(onClick={abierto=true},enabled=habilitado,modifier=Modifier.fillMaxWidth()) { Text("$etiqueta: ${opciones.find { it.first==valor }?.second ?: "Seleccionar"}") }
  DropdownMenu(expanded=abierto,onDismissRequest={abierto=false}) { opciones.forEach { (id,nombre)->DropdownMenuItem(text={Text(nombre)},onClick={elegir(id);abierto=false}) } }
  }
  if(error!=null)Text(error,color=MaterialTheme.colorScheme.error)
 }
}
