package pe.upeu.andinasalud.domain.usecase
import pe.upeu.andinasalud.domain.repository.CitaRepository
class ObtenerCitasUseCase(private val repository: CitaRepository) {
 suspend operator fun invoke() = repository.obtener().sortedBy { it.fechaHora }
}
