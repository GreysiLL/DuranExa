package pe.upeu.andinasalud.domain.usecase
import pe.upeu.andinasalud.domain.repository.CitaRepository
class ObtenerCatalogoUseCase(private val repository: CitaRepository) {
 suspend operator fun invoke() = repository.catalogo()
}
class ObservarCambiosUseCase(private val repository: CitaRepository) {
 operator fun invoke() = repository.cambios
}
