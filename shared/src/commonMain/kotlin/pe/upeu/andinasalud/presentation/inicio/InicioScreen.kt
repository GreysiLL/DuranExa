package pe.upeu.andinasalud.presentation.inicio
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.citas.CitasUiState
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.components.*
@Composable fun InicioScreen(estado: CitasUiState,reintentar:()->Unit,citas:()->Unit,solicitar:()->Unit,detalle:(Int)->Unit) {
 EstadoCarga(estado.cargando,estado.error,reintentar) {
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
 item { Text("Hola, ${estado.catalogo?.paciente?.nombre ?: "paciente"}",style=MaterialTheme.typography.headlineSmall);Text("Tu salud, más cerca.") }
 item { Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) { Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Text("Tu próxima cita",style=MaterialTheme.typography.titleLarge)
  val proxima=estado.citas.firstOrNull { it.estado is EstadoCita.Programada }
  if(proxima!=null)TarjetaCita(proxima){detalle(proxima.id)} else Text("No tienes citas Programadas.")
 } } }
 item { Button(onClick=citas,modifier=Modifier.fillMaxWidth()){Text("Mis citas")};OutlinedButton(onClick=solicitar,modifier=Modifier.fillMaxWidth()){Text("Solicitar cita")} }
 } }
}
