package pe.upeu.andinasalud.presentation.perfil
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.citas.CitasUiState
import pe.upeu.andinasalud.presentation.components.EstadoCarga
@Composable fun PerfilScreen(s: CitasUiState,reintentar:()->Unit,ajustes:()->Unit) {
 EstadoCarga(s.cargando,s.error,reintentar) {
 val p=s.catalogo?.paciente
 LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
 if(p==null)item { Text("No hay datos del paciente.") } else item {
 Text(p.nombre,style=MaterialTheme.typography.headlineSmall);Text("Documento: ${p.documento}");Text("Correo: ${p.correo}");Text("Teléfono: ${p.telefono}") }
 item { Button(onClick=ajustes){Text("Ajustes de apariencia")} }
 } }
}
@Composable fun AjustesScreen(oscuro: Boolean,cambiar:(Boolean)->Unit) {
 Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
 Text("Apariencia",style=MaterialTheme.typography.headlineSmall)
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Modo oscuro");Switch(checked=oscuro,onCheckedChange=cambiar) }
 Text("El cambio se aplica inmediatamente a todas las pantallas.")
 }
}
