package pe.upeu.andinasalud.presentation.detalle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.*
data class DetalleUiState(val cargando: Boolean=true,val cita: Cita?=null,val error: String?=null,val cancelando: Boolean=false,val mensaje: String?=null,val puedeCancelar: Boolean=false)
class DetalleCitaViewModel(private val obtener: ObtenerCitasUseCase,private val cancelar: CancelarCitaUseCase,private val reglas: ReglasCita): ViewModel() {
 private val mutable=MutableStateFlow(DetalleUiState())
 val estado=mutable.asStateFlow()
 private var job: Job?=null
 fun cargar(id: Int) {
   job?.cancel(); job=viewModelScope.launch {
     mutable.value=DetalleUiState()
     try { val cita=obtener().find { it.id==id }; mutable.value=DetalleUiState(cargando=false,cita=cita,puedeCancelar=cita?.let { reglas.puedeCancelar(it) } ?: false) }
     catch(e: CancellationException){throw e}
     catch(e: Exception){mutable.value=DetalleUiState(cargando=false,error=e.message)}
   }
 }
 fun cancelar() {
   if(mutable.value.cancelando) return
   val cita=mutable.value.cita ?: return
   mutable.update { it.copy(cancelando=true,error=null) }
   viewModelScope.launch {
     try { cancelar(cita.id); val actual=obtener().find { it.id==cita.id }; mutable.value=DetalleUiState(cargando=false,cita=actual,mensaje="Cita cancelada correctamente.") }
     catch(e: CancellationException){throw e}
     catch(e: Exception){mutable.update { it.copy(cancelando=false,error=e.message) }}
   }
 }
}
