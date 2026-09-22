package pe.upeu.andinasalud

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.datetime.*
import kotlin.test.*
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ParteDosTest {
    private val reloj=RelojFijo()
    private val reglas=ReglasCita(reloj)
    private val dispatcher=StandardTestDispatcher()
    private lateinit var store: ViewModelStore
    @BeforeTest fun preparar(){Dispatchers.setMain(dispatcher);store=ViewModelStore()}
    @AfterTest fun limpiar(){store.clear();Dispatchers.resetMain()}
    private fun reprogramar(repo: CitaRepositoryFake, op: OperacionesCitas=OperacionesCitas()) = ReprogramarCitaUseCase(repo,reglas,op,reloj)
    private fun lista(repo: CitaRepositoryFake)=CitasViewModel(ObtenerCitasUseCase(repo),ObtenerCatalogoUseCase(repo),ObservarCambiosUseCase(repo),reloj,reglas).also {store.put("lista",it)}

    @Test fun hoyCombinaEstadoBusquedaYFecha()=runTest(dispatcher){
        val repo=CitaRepositoryFake(reloj)
        val cita=repo.obtener().first()
        repo.actualizar(cita.copy(fechaHora=LocalDateTime.parse("2026-09-22T18:00")))
        val vm=lista(repo);advanceUntilIdle()
        vm.cambiarHoy();vm.filtrar("Programada");vm.buscar("IVAN")
        assertEquals(listOf(cita.id),vm.visibles(vm.estado.value).map {it.id})
        vm.filtrar("Atendida");assertTrue(vm.visibles(vm.estado.value).isEmpty())
        vm.filtrar("Programada");vm.buscar("inexistente");assertTrue(vm.visibles(vm.estado.value).isEmpty())
        vm.buscar("");vm.cambiarHoy();assertEquals(3,vm.visibles(vm.estado.value).size)
    }
    @Test fun hoyUsaFechaLocalNoUTC()=runTest(dispatcher){
        val r=object:Reloj {override fun ahora()=kotlin.time.Instant.parse("2026-09-23T02:00:00Z");override val zona=TimeZone.of("America/Lima")}
        val repo=CitaRepositoryFake(r);val cita=repo.obtener().first()
        repo.actualizar(cita.copy(fechaHora=LocalDateTime.parse("2026-09-22T23:00")))
        val vm=CitasViewModel(ObtenerCitasUseCase(repo),ObtenerCatalogoUseCase(repo),ObservarCambiosUseCase(repo),r,ReglasCita(r));store.put("lista",vm)
        advanceUntilIdle();vm.cambiarHoy();assertEquals(listOf(cita.id),vm.visibles(vm.estado.value).map{it.id})
    }
    @Test fun contadorIgnoraFiltrosYSeActualizaTrasCancelar()=runTest(dispatcher){
        val repo=CitaRepositoryFake(reloj);val vm=lista(repo);advanceUntilIdle()
        assertEquals(3,vm.estado.value.programadas);assertFalse(vm.estado.value.puedeSolicitar)
        vm.buscar("no existe");vm.cambiarHoy();assertEquals(3,vm.estado.value.programadas)
        CancelarCitaUseCase(repo,reglas,OperacionesCitas())(1);advanceUntilIdle()
        assertEquals(2,vm.estado.value.programadas);assertTrue(vm.estado.value.puedeSolicitar)
        SolicitarCitaUseCase(repo,reglas,OperacionesCitas())("Medicina General","N","2026-10-01","10:00","Consulta general")
        advanceUntilIdle();assertEquals(3,vm.estado.value.programadas);assertFalse(vm.estado.value.puedeSolicitar)
    }
    @Test fun modalidadesSeConservanEnRepositorio()=runTest {
        val repo=CitaRepositoryFake(reloj);val op=OperacionesCitas()
        CancelarCitaUseCase(repo,reglas,op)(1)
        val c=SolicitarCitaUseCase(repo,reglas,op)("Medicina General","N","2026-10-01","10:00","Consulta general",ModalidadAtencion.TELECONSULTA)
        assertEquals(ModalidadAtencion.TELECONSULTA,repo.obtener().first{it.id==c.id}.modalidad)
        assertEquals(setOf(ModalidadAtencion.PRESENCIAL,ModalidadAtencion.TELECONSULTA),repo.obtener().map{it.modalidad}.toSet())
    }
    @Test fun formularioEnviaModalidadElegida()=runTest(dispatcher){
        val repo=CitaRepositoryFake(reloj);val op=OperacionesCitas();CancelarCitaUseCase(repo,reglas,op)(1)
        val vm=SolicitudViewModel(ObtenerCatalogoUseCase(repo),SolicitarCitaUseCase(repo,reglas,op));store.put("form",vm)
        advanceUntilIdle();vm.cambiar("modalidad","TELECONSULTA");vm.cambiar("especialidad","Medicina General");vm.cambiar("sede","N")
        vm.cambiar("fecha","2026-10-01");vm.cambiar("hora","11:00");vm.cambiar("motivo","Consulta general")
        vm.registrar();advanceUntilIdle()
        assertEquals(ModalidadAtencion.TELECONSULTA,repo.obtener().first{it.id==vm.estado.value.registrada}.modalidad)
    }
    @Test fun reprogramaConTresSinCrearOtraCitaYConservaDatos()=runTest {
        val repo=CitaRepositoryFake(reloj);val original=repo.obtener().first()
        val nueva=reprogramar(repo)(1,"2026-10-01","12:30")
        assertEquals(original.id,nueva.id);assertEquals(original.modalidad,nueva.modalidad)
        assertEquals(original.motivo,nueva.motivo);assertEquals(original.medico,nueva.medico)
        assertEquals(original.estado,nueva.estado);assertEquals(original.sede,nueva.sede)
        assertEquals(6,repo.obtener().size);assertEquals(3,reglas.contarProgramadas(repo.obtener(),original.pacienteId))
        assertEquals(original.fechaHora,nueva.cambiosHorario.single().anterior)
        assertEquals(nueva.fechaHora,nueva.cambiosHorario.single().nueva)
        assertEquals(reloj.ahora(),nueva.cambiosHorario.single().registradoEn)
    }
    @Test fun reprogramacionesAcumulanHistorial()=runTest {
        val repo=CitaRepositoryFake(reloj);val cambiar=reprogramar(repo)
        val primera=cambiar(1,"2026-10-01","10:00");val segunda=cambiar(1,"2026-10-02","11:00")
        assertEquals(2,segunda.cambiosHorario.size);assertEquals(primera.fechaHora,segunda.cambiosHorario.last().anterior)
    }
    @Test fun reprogramacionRechazaPasadoSinModificarDatos()=runTest {
        val repo=CitaRepositoryFake(reloj);val antes=repo.obtener()
        assertTrue("fecha" in assertFailsWith<ErrorCampos>{reprogramar(repo)(1,"2026-09-21","10:00")}.campos)
        assertEquals(antes,repo.obtener());assertEquals(0,repo.cambios.value)
    }
    @Test fun reprogramacionRechazaDuplicado()=runTest {
        val repo=CitaRepositoryFake(reloj);val segunda=repo.obtener()[1]
        assertTrue("hora" in assertFailsWith<ErrorCampos>{reprogramar(repo)(1,segunda.fechaHora.date.toString(),segunda.fechaHora.time.toString())}.campos)
        assertTrue(repo.obtener().first().cambiosHorario.isEmpty())
    }
    @Test fun reprogramacionRechazaEstadoAtendidaYCancelada()=runTest {
        val repo=CitaRepositoryFake(reloj)
        for(id in listOf(4,6))assertFailsWith<ErrorCampos>{reprogramar(repo)(id,"2026-10-01","10:00")}
    }
    @Test fun reprogramacionRechazaIdInexistente()=runTest {
        assertFailsWith<ErrorCampos>{reprogramar(CitaRepositoryFake(reloj))(999,"2026-10-01","10:00")}
    }
    @Test fun reprogramacionValidaFormatoPorCampo()=runTest {
        val repo=CitaRepositoryFake(reloj)
        assertEquals(setOf("fecha","hora"),assertFailsWith<ErrorCampos>{reprogramar(repo)(1,"2026-02-30","99:00")}.campos.keys)
    }
    @Test fun reprogramacionSinCambioNoAgregaHistorial()=runTest {
        val repo=CitaRepositoryFake(reloj);val cita=repo.obtener().first()
        assertFailsWith<ErrorCampos>{reprogramar(repo)(1,cita.fechaHora.date.toString(),cita.fechaHora.time.toString())}
        assertEquals(0,repo.cambios.value)
    }
    @Test fun reprogramarNoAplicaRestriccionDeCancelacion()=runTest {
        val repo=CitaRepositoryFake(reloj)
        val cita=reprogramar(repo)(1,"2026-09-22","16:00")
        assertFalse(reglas.puedeCancelar(cita));assertTrue(reglas.puedeReprogramar(cita))
    }
    @Test fun reprogramacionesConcurrentesNoDuplicanHorario()=runTest {
        val repo=CitaRepositoryFake(reloj);val cambiar=reprogramar(repo)
        val a=async{runCatching{cambiar(1,"2026-10-01","10:00")}}
        val b=async{runCatching{cambiar(2,"2026-10-01","10:00")}}
        assertEquals(1,listOf(a.await(),b.await()).count{it.isSuccess})
    }
    @Test fun detalleMuestraErroresYDespuesHistorial()=runTest(dispatcher){
        val repo=CitaRepositoryFake(reloj);val op=OperacionesCitas()
        val vm=DetalleCitaViewModel(ObtenerCitasUseCase(repo),CancelarCitaUseCase(repo,reglas,op),reglas,reprogramar(repo,op));store.put("detalle",vm)
        vm.cargar(1);advanceUntilIdle();vm.abrirReprogramacion();vm.cambiarHorario("fecha","incorrecta")
        vm.reprogramar();advanceUntilIdle();assertTrue("fecha" in vm.estado.value.errores);assertTrue(vm.estado.value.editando)
        vm.cambiarHorario("fecha","2026-10-01");vm.reprogramar();vm.reprogramar();advanceUntilIdle()
        assertFalse(vm.estado.value.editando);assertEquals(1,vm.estado.value.cita!!.cambiosHorario.size)
        assertEquals("Cita reprogramada correctamente.",vm.estado.value.mensaje)
    }
}
