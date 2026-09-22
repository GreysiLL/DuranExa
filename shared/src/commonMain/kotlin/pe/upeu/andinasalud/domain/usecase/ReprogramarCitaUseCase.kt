package pe.upeu.andinasalud.domain.usecase
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ReprogramarCitaUseCase(
    private val repository: CitaRepository, private val reglas: ReglasCita,
    private val operaciones: OperacionesCitas, private val reloj: Reloj
) {
    suspend operator fun invoke(id: Int, fecha: String, hora: String): Cita = operaciones.mutex.withLock {
        val citas = repository.obtener()
        val original = citas.find { it.id == id } ?: throw ErrorCampos(mapOf("general" to "No se encontró la cita."))
        if (!reglas.puedeReprogramar(original))
            throw ErrorCampos(mapOf("general" to "Solo se reprograman citas Programadas."))
        val errores = mutableMapOf<String, String>()
        val horario = leerFechaHora(fecha, hora, errores)
        if (errores.isNotEmpty()) throw ErrorCampos(errores)
        val nueva = original.copy(fechaHora = requireNotNull(horario))
        // La cita ya ocupa una plaza: no debe contarse ni compararse consigo misma.
        reglas.validarSolicitud(nueva, citas.filter { it.id != id })
        if (nueva.fechaHora == original.fechaHora)
            throw ErrorCampos(mapOf("fecha" to "Elige una fecha u hora diferente."))
        val actualizada = nueva.copy(cambiosHorario = original.cambiosHorario +
            CambioHorario(original.fechaHora, nueva.fechaHora, reloj.ahora()))
        repository.actualizar(actualizada)
        actualizada
    }
}
