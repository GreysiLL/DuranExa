package pe.upeu.andinasalud.domain.usecase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.*
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
// Una puerta compartida evita que dos solicitudes simultaneas superen RN-02 o RN-05.
class OperacionesCitas { val mutex = Mutex() }
class SolicitarCitaUseCase(private val repository: CitaRepository, private val reglas: ReglasCita, private val operaciones: OperacionesCitas) {
 suspend operator fun invoke(especialidad: String, sedeId: String, fecha: String, hora: String, motivo: String, modalidad: ModalidadAtencion = ModalidadAtencion.PRESENCIAL): Cita = operaciones.mutex.withLock {
   val catalogo = repository.catalogo()
   val errores = mutableMapOf<String, String>()
   if(especialidad !in catalogo.especialidades) errores["especialidad"] = "Selecciona una especialidad."
   val sede = catalogo.sedes.find { it.id == sedeId }
   if(sede == null) errores["sede"] = "Selecciona una sede."
   val fechaHora = leerFechaHora(fecha, hora, errores)
   if(motivo.trim().length !in 10..200) errores["motivo"] = "Escribe entre 10 y 200 caracteres."
   if(errores.isNotEmpty()) throw ErrorCampos(errores)
   val medico = catalogo.medicos.firstOrNull { it.especialidad == especialidad && sedeId in it.sedes }
     ?: throw ErrorCampos(mapOf("sede" to "No hay medico de esta especialidad en la sede."))
   val cita = Cita(0,catalogo.paciente.id,medico,requireNotNull(sede),requireNotNull(fechaHora),motivo.trim(),EstadoCita.Programada(true),modalidad)
   reglas.validarSolicitud(cita,repository.obtener())
   repository.guardar(cita)
 }
}
