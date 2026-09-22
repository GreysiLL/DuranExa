package pe.upeu.andinasalud.domain.repository
import kotlinx.coroutines.flow.StateFlow
import pe.upeu.andinasalud.domain.model.*
interface CitaRepository {
 val cambios: StateFlow<Int>
 suspend fun catalogo(): Catalogo
 suspend fun obtener(): List<Cita>
 suspend fun guardar(cita: Cita): Cita
 suspend fun actualizar(cita: Cita)
}
