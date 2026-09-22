package pe.upeu.andinasalud.di
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel
val appModule=module {
 single<Reloj> { RelojSistema() }
 single<CitaRepository> { CitaRepositoryFake(get()) }
 single { OperacionesCitas() }
 factory { ReglasCita(get()) }
 factory { ObtenerCitasUseCase(get()) }
 factory { ObtenerCatalogoUseCase(get()) }
 factory { ObservarCambiosUseCase(get()) }
 factory { SolicitarCitaUseCase(get(),get(),get()) }
 factory { CancelarCitaUseCase(get(),get(),get()) }
 factory { ReprogramarCitaUseCase(get(),get(),get(),get()) }
 viewModel { CitasViewModel(get(),get(),get(),get(),get()) }
 viewModel { DetalleCitaViewModel(get(),get(),get(),get()) }
 viewModel { SolicitudViewModel(get(),get()) }
}
fun initKoin() { startKoin { modules(appModule) } }
