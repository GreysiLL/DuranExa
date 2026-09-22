package pe.upeu.andinasalud.domain.usecase
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
class CancelarCitaUseCase(private val repository: CitaRepository, private val reglas: ReglasCita, private val operaciones: OperacionesCitas) {
 suspend operator fun invoke(id: Int) = operaciones.mutex.withLock {
   val cita = repository.obtener().find { it.id == id } ?: error("No se encontro la cita.")
   reglas.validarCancelacion(cita)
   repository.actualizar(cita.copy(estado=EstadoCita.Cancelada("Cancelada por el paciente desde la aplicacion",true)))
 }
}
