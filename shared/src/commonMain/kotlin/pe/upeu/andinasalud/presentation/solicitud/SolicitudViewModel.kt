package pe.upeu.andinasalud.presentation.solicitud
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.usecase.*
data class SolicitudUiState(val cargando: Boolean=true,val catalogo: Catalogo?=null,val errorCarga: String?=null,
 val especialidad: String="",val sede: String="",val fecha: String="",val hora: String="",val motivo: String="",
 val errores: Map<String,String> = emptyMap(),val guardando: Boolean=false,val registrada: Int?=null)
class SolicitudViewModel(private val catalogo: ObtenerCatalogoUseCase,private val solicitar: SolicitarCitaUseCase): ViewModel() {
 private val mutable=MutableStateFlow(SolicitudUiState())
 val estado=mutable.asStateFlow()
 init { cargar() }
 fun cargar() { viewModelScope.launch {
   mutable.update { it.copy(cargando=true,errorCarga=null) }
   try { val c=catalogo(); mutable.update { it.copy(cargando=false,catalogo=c) } }
   catch(e: CancellationException){throw e}
   catch(e: Exception){mutable.update { it.copy(cargando=false,errorCarga=e.message ?: "No se pudo cargar el formulario.") }}
 } }
 fun cambiar(campo: String,valor: String) { if(mutable.value.guardando)return; mutable.update {
   val s=when(campo){"especialidad"->it.copy(especialidad=valor);"sede"->it.copy(sede=valor);"fecha"->it.copy(fecha=valor);"hora"->it.copy(hora=valor);else->it.copy(motivo=valor)}
   s.copy(errores=s.errores-campo-"general",registrada=null)
 } }
 fun registrar() {
   if(mutable.value.guardando || mutable.value.registrada!=null)return
   val s=mutable.value; mutable.update { it.copy(guardando=true,errores=emptyMap()) }
   viewModelScope.launch {
     try { val cita=solicitar(s.especialidad,s.sede,s.fecha,s.hora,s.motivo);mutable.update { it.copy(guardando=false,registrada=cita.id) } }
     catch(e: CancellationException){throw e}
     catch(e: ErrorCampos){mutable.update { it.copy(guardando=false,errores=e.campos) }}
     catch(e: Exception){mutable.update { it.copy(guardando=false,errores=mapOf("general" to (e.message ?: "No se pudo registrar."))) }}
   }
 }
}
