package pe.upeu.andinasalud.domain.usecase
import kotlinx.datetime.*

// Solicitud y reprogramación usan el mismo formato y los mismos errores por campo.
fun leerFechaHora(fecha: String, hora: String, errores: MutableMap<String, String>): LocalDateTime? {
    val dia = try { LocalDate.parse(fecha.trim()) } catch (_: IllegalArgumentException) { null }
    val tiempo = try { LocalTime.parse(hora.trim()) } catch (_: IllegalArgumentException) { null }
    if (dia == null) errores["fecha"] = "Usa una fecha valida: AAAA-MM-DD."
    if (tiempo == null || !Regex("[0-9]{2}:[0-9]{2}").matches(hora.trim()))
        errores["hora"] = "Usa una hora valida: HH:MM."
    return if (dia != null && tiempo != null) LocalDateTime(dia, tiempo) else null
}
