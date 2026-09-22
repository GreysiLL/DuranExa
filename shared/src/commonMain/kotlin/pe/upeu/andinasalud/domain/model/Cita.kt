package pe.upeu.andinasalud.domain.model

data class Cita(val id: Int, val pacienteId: String, val medico: Medico, val sede: Sede,
 val fechaHora: kotlinx.datetime.LocalDateTime, val motivo: String, val estado: EstadoCita)
