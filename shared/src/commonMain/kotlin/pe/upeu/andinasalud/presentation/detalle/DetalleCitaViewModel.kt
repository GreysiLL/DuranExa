package pe.upeu.andinasalud.presentation.detalle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.*

data class DetalleUiState(
    val cargando: Boolean=true, val cita: Cita?=null, val error: String?=null,
    val cancelando: Boolean=false, val mensaje: String?=null, val puedeCancelar: Boolean=false,
    val puedeReprogramar: Boolean=false, val editando: Boolean=false, val guardando: Boolean=false,
    val fecha: String="", val hora: String="", val errores: Map<String,String> = emptyMap()
)
class DetalleCitaViewModel(
    private val obtener: ObtenerCitasUseCase, private val cancelar: CancelarCitaUseCase,
    private val reglas: ReglasCita, private val cambiarFecha: ReprogramarCitaUseCase
): ViewModel() {
    private val mutable=MutableStateFlow(DetalleUiState())
    val estado=mutable.asStateFlow()
    private var job: Job?=null
    private fun mostrar(cita: Cita?, mensaje: String?=null) = DetalleUiState(
        cargando=false,cita=cita,mensaje=mensaje,
        puedeCancelar=cita?.let { reglas.puedeCancelar(it) } ?: false,
        puedeReprogramar=cita?.let { reglas.puedeReprogramar(it) } ?: false)
    fun cargar(id: Int) {
        job?.cancel();job=viewModelScope.launch {
            mutable.value=DetalleUiState()
            try { mutable.value=mostrar(obtener().find { it.id==id }) }
            catch(e: CancellationException){throw e}
            catch(e: Exception){mutable.value=DetalleUiState(cargando=false,error=e.message)}
        }
    }
    fun cancelar() {
        if(mutable.value.cancelando || mutable.value.guardando)return
        val cita=mutable.value.cita ?: return
        mutable.update { it.copy(cancelando=true,error=null) }
        job=viewModelScope.launch {
            try { cancelar(cita.id);mutable.value=mostrar(obtener().find { it.id==cita.id },"Cita cancelada correctamente.") }
            catch(e: CancellationException){throw e}
            catch(e: Exception){mutable.update { it.copy(cancelando=false,error=e.message) }}
        }
    }
    fun abrirReprogramacion() {
        val s=mutable.value;val cita=s.cita ?: return
        if(!s.puedeReprogramar || s.cancelando || s.guardando)return
        mutable.update { it.copy(editando=true,fecha=cita.fechaHora.date.toString(),
            hora=cita.fechaHora.hour.toString().padStart(2,'0') + ":" + cita.fechaHora.minute.toString().padStart(2,'0'),
            errores=emptyMap(),error=null,mensaje=null) }
    }
    fun cerrarReprogramacion() { if(!mutable.value.guardando)mutable.update { it.copy(editando=false,errores=emptyMap()) } }
    fun cambiarHorario(campo: String,valor: String) {
        if(mutable.value.guardando)return
        mutable.update { (if(campo=="fecha")it.copy(fecha=valor)else it.copy(hora=valor)).copy(errores=it.errores-campo-"general") }
    }
    fun reprogramar() {
        val s=mutable.value;val cita=s.cita ?: return
        if(s.guardando || s.cancelando || !s.editando)return
        mutable.update { it.copy(guardando=true,errores=emptyMap()) }
        job=viewModelScope.launch {
            try { mutable.value=mostrar(cambiarFecha(cita.id,s.fecha,s.hora),"Cita reprogramada correctamente.") }
            catch(e: CancellationException){throw e}
            catch(e: ErrorCampos){mutable.update { it.copy(guardando=false,errores=e.campos) }}
            catch(e: Exception){mutable.update { it.copy(guardando=false,errores=mapOf("general" to (e.message ?: "No se pudo reprogramar."))) }}
        }
    }
}
