package pe.upeu.andinasalud.domain.usecase
import kotlinx.datetime.*
import pe.upeu.andinasalud.domain.model.*
import kotlin.time.Duration.Companion.hours
class ErrorCampos(val campos: Map<String, String>) : IllegalArgumentException(campos.values.first())
class ReglasCita(private val reloj: Reloj) {
 companion object { const val MAX_PROGRAMADAS = 3 }
 fun puedeSolicitar(citas: List<Cita>, pacienteId: String) =
   citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada } < MAX_PROGRAMADAS
 fun validarSolicitud(cita: Cita, existentes: List<Cita>) {
   val errores = mutableMapOf<String, String>()
   // RN-01: se compara el instante completo, no solo el dia.
   if(cita.fechaHora.toInstant(reloj.zona) < reloj.ahora()) errores["fecha"] = "La fecha y hora deben ser futuras."
   if(!puedeSolicitar(existentes, cita.pacienteId)) errores["general"] = "Ya tienes tres citas Programadas."
   if(cita.motivo.trim().length !in 10..200) errores["motivo"] = "Escribe entre 10 y 200 caracteres."
   if(existentes.any { it.pacienteId == cita.pacienteId && it.estado is EstadoCita.Programada && it.fechaHora == cita.fechaHora })
     errores["hora"] = "Ya tienes una cita Programada en ese horario."
   if(errores.isNotEmpty()) throw ErrorCampos(errores)
 }
 fun puedeCancelar(cita: Cita) = cita.estado is EstadoCita.Programada &&
   cita.fechaHora.toInstant(reloj.zona) - reloj.ahora() > 24.hours
 fun validarCancelacion(cita: Cita) {
   require(puedeCancelar(cita)) { "Solo puedes cancelar citas Programadas con mas de 24 horas de anticipacion." }
 }
}
