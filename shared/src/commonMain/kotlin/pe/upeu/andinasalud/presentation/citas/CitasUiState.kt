package pe.upeu.andinasalud.presentation.citas
import pe.upeu.andinasalud.domain.model.*
data class CitasUiState(
 val cargando: Boolean = true, val error: String? = null,
 val catalogo: Catalogo? = null, val citas: List<Cita> = emptyList(),
 val busqueda: String = "", val filtro: String = "Todas",
 val soloHoy: Boolean = false, val programadas: Int = 0, val puedeSolicitar: Boolean = false
)
fun normalizar(texto: String): String = texto.lowercase().map { letra ->
 when(letra) { 'á','à','ä' -> 'a'; 'é','è','ë' -> 'e'; 'í','ì','ï' -> 'i'; 'ó','ò','ö' -> 'o'; 'ú','ù','ü' -> 'u'; else -> letra }
}.joinToString("")
fun nombreEstado(estado: EstadoCita): String = when(estado) {
 is EstadoCita.Programada -> "Programada"
 is EstadoCita.Atendida -> "Atendida"
 is EstadoCita.Cancelada -> "Cancelada"
}
fun filtrarCitas(citas: List<Cita>, filtro: String, texto: String, hoy: kotlinx.datetime.LocalDate? = null): List<Cita> {
 val consulta=normalizar(texto.trim())
 return citas.filter { (hoy == null || it.fechaHora.date == hoy) && (filtro=="Todas" || nombreEstado(it.estado)==filtro) &&
 (normalizar(it.medico.nombre).contains(consulta) || normalizar(it.medico.especialidad).contains(consulta)) }.sortedBy { it.fechaHora }
}
