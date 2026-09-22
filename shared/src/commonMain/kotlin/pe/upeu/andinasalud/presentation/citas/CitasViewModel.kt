package pe.upeu.andinasalud.presentation.citas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pe.upeu.andinasalud.domain.usecase.*
class CitasViewModel(private val obtener: ObtenerCitasUseCase, private val catalogo: ObtenerCatalogoUseCase, cambios: ObservarCambiosUseCase) : ViewModel() {
 private val mutable = MutableStateFlow(CitasUiState())
 val estado = mutable.asStateFlow()
 private var carga: Job? = null
 init { viewModelScope.launch { cambios().collect { cargar() } } }
 fun cargar() {
   carga?.cancel()
   carga=viewModelScope.launch {
     mutable.update { it.copy(cargando=true,error=null) }
     try {
       coroutineScope {
         val datos=async { obtener() }; val ficha=async { catalogo() }
         val citas=datos.await(); val c=ficha.await()
         mutable.update { it.copy(cargando=false,citas=citas,catalogo=c) }
       }
     } catch(e: CancellationException) { throw e }
     catch(e: Exception) { mutable.update { it.copy(cargando=false,error=e.message ?: "No se pudieron cargar los datos.") } }
   }
 }
 fun buscar(texto: String) { mutable.update { it.copy(busqueda=texto) } }
 fun filtrar(valor: String) { mutable.update { it.copy(filtro=valor) } }
 fun visibles(estado: CitasUiState) = filtrarCitas(estado.citas,estado.filtro,estado.busqueda)
}
