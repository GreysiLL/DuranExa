package pe.upeu.andinasalud.presentation.citas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.presentation.components.*
@Composable fun CitasScreen(estado: CitasUiState,visibles: List<Cita>,buscar:(String)->Unit,filtrar:(String)->Unit,reintentar:()->Unit,solicitar:()->Unit,detalle:(Int)->Unit,hoy:()->Unit={}) {
 EstadoCarga(estado.cargando,estado.error,reintentar) {
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
 item { OutlinedTextField(value=estado.busqueda,onValueChange=buscar,label={Text("Buscar especialidad o médico")},modifier=Modifier.fillMaxWidth()) }
 item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) { items(listOf("Todas","Programada","Atendida","Cancelada")) { filtro -> FilterChip(selected=estado.filtro==filtro,onClick={filtrar(filtro)},label={Text(filtro)}) } } }
 item { FilterChip(selected=estado.soloHoy,onClick=hoy,label={Text("Hoy")}) }
 item { Button(onClick=solicitar,enabled=estado.puedeSolicitar,modifier=Modifier.fillMaxWidth()){Text("Solicitar cita")} }
 if(!estado.puedeSolicitar) item { Text("Has alcanzado el límite de citas programadas.") }
 if(visibles.isEmpty()) item { Text("No hay citas para esta búsqueda o estado.") }
 items(visibles,key={it.id}) { cita -> TarjetaCita(cita){detalle(cita.id)} }
 } }
}
