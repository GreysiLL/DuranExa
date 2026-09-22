package pe.upeu.andinasalud

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import kotlin.test.*
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

private class FuentePrueba : CitaRepository {
    override val cambios = MutableStateFlow(0)
    var fallar = false
    var registros = CitasSimuladas.citas(RelojFijo())
    override suspend fun catalogo(): Catalogo {
        delay(800)
        if (fallar) error("Error de prueba")
        return CitasSimuladas.catalogo
    }
    override suspend fun obtener(): List<Cita> {
        delay(800)
        if (fallar) error("Error de prueba")
        return registros
    }
    override suspend fun guardar(cita: Cita): Cita {
        val nueva = cita.copy(id=registros.size+1)
        registros = registros+nueva
        cambios.value++
        return nueva
    }
    override suspend fun actualizar(cita: Cita) {
        registros = registros.map { if (it.id==cita.id) cita else it }
        cambios.value++
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class EstadosPantallaTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var store: ViewModelStore
    @BeforeTest fun preparar() { Dispatchers.setMain(dispatcher); store=ViewModelStore() }
    @AfterTest fun limpiar() { store.clear(); Dispatchers.resetMain() }
    private fun listado(repo: FuentePrueba): CitasViewModel =
        CitasViewModel(ObtenerCitasUseCase(repo),ObtenerCatalogoUseCase(repo),ObservarCambiosUseCase(repo)).also { store.put("lista",it) }

    @Test fun listaPasaDeCargaAContenido() = runTest(dispatcher) {
        val vm=listado(FuentePrueba())
        assertTrue(vm.estado.value.cargando)
        advanceTimeBy(799); runCurrent()
        assertTrue(vm.estado.value.cargando)
        advanceUntilIdle()
        assertFalse(vm.estado.value.cargando)
        assertEquals(6,vm.estado.value.citas.size)
    }
    @Test fun listaVacia() = runTest(dispatcher) {
        val vm=listado(FuentePrueba().apply { registros=emptyList() })
        advanceUntilIdle()
        assertTrue(vm.estado.value.citas.isEmpty())
        assertNull(vm.estado.value.error)
    }
    @Test fun errorYReintentoRecuperanContenido() = runTest(dispatcher) {
        val repo=FuentePrueba().apply { fallar=true }; val vm=listado(repo)
        advanceUntilIdle(); assertEquals("Error de prueba",vm.estado.value.error)
        repo.fallar=false;vm.cargar();advanceUntilIdle()
        assertNull(vm.estado.value.error);assertEquals(6,vm.estado.value.citas.size)
    }
    @Test fun detalleInexistenteEsVacio() = runTest(dispatcher) {
        val repo=FuentePrueba()
        val vm=DetalleCitaViewModel(ObtenerCitasUseCase(repo),CancelarCitaUseCase(repo,ReglasCita(RelojFijo()),OperacionesCitas()),ReglasCita(RelojFijo()),ReprogramarCitaUseCase(repo,ReglasCita(RelojFijo()),OperacionesCitas(),RelojFijo()))
        store.put("detalle",vm);vm.cargar(999);advanceUntilIdle()
        assertFalse(vm.estado.value.cargando);assertNull(vm.estado.value.cita)
    }
    @Test fun formularioComunicaErroresPorCampo() = runTest(dispatcher) {
        val repo=FuentePrueba()
        val vm=SolicitudViewModel(ObtenerCatalogoUseCase(repo),SolicitarCitaUseCase(repo,ReglasCita(RelojFijo()),OperacionesCitas()))
        store.put("form",vm);advanceUntilIdle();vm.registrar();advanceUntilIdle()
        assertEquals(setOf("especialidad","sede","fecha","hora","motivo"),vm.estado.value.errores.keys)
        assertFalse(vm.estado.value.guardando)
    }
    @Test fun formularioBloqueaDoblePulsacion() = runTest(dispatcher) {
        val repo=FuentePrueba().apply { registros=emptyList() }
        val vm=SolicitudViewModel(ObtenerCatalogoUseCase(repo),SolicitarCitaUseCase(repo,ReglasCita(RelojFijo()),OperacionesCitas()))
        store.put("form",vm);advanceUntilIdle()
        vm.cambiar("especialidad","Medicina General");vm.cambiar("sede","N")
        vm.cambiar("fecha","2026-10-01");vm.cambiar("hora","10:00");vm.cambiar("motivo","Consulta general")
        vm.registrar();vm.registrar();advanceUntilIdle()
        assertEquals(1,repo.registros.size);assertNotNull(vm.estado.value.registrada)
    }
    @Test fun destruccionCancelaCarga() = runTest(dispatcher) {
        val vm=listado(FuentePrueba());runCurrent();store.clear();advanceUntilIdle()
        assertTrue(vm.estado.value.cargando)
        assertNull(vm.estado.value.error)
    }
}
