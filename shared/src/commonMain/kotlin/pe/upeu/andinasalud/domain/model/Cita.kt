package pe.upeu.andinasalud.domain.model
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant

data class CambioHorario(
    val anterior: LocalDateTime,
    val nueva: LocalDateTime,
    val registradoEn: Instant
)
data class Cita(
    val id: Int, val pacienteId: String, val medico: Medico, val sede: Sede,
    val fechaHora: LocalDateTime, val motivo: String, val estado: EstadoCita,
    val modalidad: ModalidadAtencion = ModalidadAtencion.PRESENCIAL,
    val cambiosHorario: List<CambioHorario> = emptyList()
)
