package pe.upeu.andinasalud.data.repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.Reloj
class CitaRepositoryFake(reloj: Reloj) : CitaRepository {
 private val mutex = Mutex()
 private val citas = CitasSimuladas.citas(reloj).toMutableList()
 private var siguienteId = 7
 private val version = MutableStateFlow(0)
 override val cambios = version.asStateFlow()
 override suspend fun catalogo(): Catalogo { delay(800); return CitasSimuladas.catalogo }
 override suspend fun obtener(): List<Cita> { delay(800); return mutex.withLock { citas.toList() } }
 override suspend fun guardar(cita: Cita): Cita = mutex.withLock {
   val nueva = cita.copy(id=siguienteId++)
   citas.add(nueva); version.value++; nueva
 }
 override suspend fun actualizar(cita: Cita) = mutex.withLock {
   val posicion = citas.indexOfFirst { it.id == cita.id }
   require(posicion >= 0) { "No se encontro la cita." }
   citas[posicion]=cita; version.value++
   Unit
 }
}
