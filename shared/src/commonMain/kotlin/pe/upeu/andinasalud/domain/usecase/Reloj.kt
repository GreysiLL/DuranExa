package pe.upeu.andinasalud.domain.usecase
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.*
interface Reloj { fun ahora(): Instant; val zona: TimeZone }
class RelojSistema : Reloj {
 override fun ahora() = Clock.System.now()
 override val zona get() = TimeZone.currentSystemDefault()
}
