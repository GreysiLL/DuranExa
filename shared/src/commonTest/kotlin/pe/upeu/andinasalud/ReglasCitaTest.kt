package pe.upeu.andinasalud

import kotlin.test.*
import kotlin.time.Instant
import kotlinx.datetime.*
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.presentation.citas.filtrarCitas

class RelojFijo : Reloj {
    override fun ahora() = Instant.parse("2026-09-22T15:00:00Z")
    override val zona = TimeZone.UTC
}

class ReglasCitaTest {
    private val reloj = RelojFijo()
    private val reglas = ReglasCita(reloj)
    private fun cita(fecha: String = "2026-09-24T15:00", motivo: String = "Consulta general") =
        Cita(1,"P-0417",CitasSimuladas.medicos.first(),CitasSimuladas.sedes.first(),
            LocalDateTime.parse(fecha),motivo,EstadoCita.Programada(true))

    @Test fun solicitudValida() { reglas.validarSolicitud(cita(),emptyList()) }
    @Test fun rechazaPasado() { assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita("2026-09-22T14:59"),emptyList()) } }
    @Test fun permiteMomentoActualSegunRN01() { reglas.validarSolicitud(cita("2026-09-22T15:00"),emptyList()) }
    @Test fun rechazaCuartaProgramada() {
        assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita(),(1..3).map { cita().copy(id=it) }) }
    }
    @Test fun atendidasNoConsumenLimite() {
        assertTrue(reglas.puedeSolicitar((1..5).map { cita().copy(id=it,estado=EstadoCita.Atendida("Control")) },"P-0417"))
    }
    @Test fun limiteEsPorPaciente() {
        assertTrue(reglas.puedeSolicitar((1..3).map { cita().copy(id=it,pacienteId="Otro") },"P-0417"))
    }
    @Test fun motivoNueveInvalido() { assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita(motivo="a".repeat(9)),emptyList()) } }
    @Test fun motivoDiezValido() { reglas.validarSolicitud(cita(motivo="a".repeat(10)),emptyList()) }
    @Test fun motivoDoscientosValido() { reglas.validarSolicitud(cita(motivo="a".repeat(200)),emptyList()) }
    @Test fun motivoDoscientosUnoInvalido() { assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita(motivo="a".repeat(201)),emptyList()) } }
    @Test fun espaciosNoSonMotivo() { assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita(motivo=" ".repeat(15)),emptyList()) } }
    @Test fun rechazaHorarioDuplicado() { assertFailsWith<ErrorCampos> { reglas.validarSolicitud(cita(),listOf(cita())) } }
    @Test fun canceladaNoBloqueaHorario() { reglas.validarSolicitud(cita(),listOf(cita().copy(estado=EstadoCita.Cancelada("Viaje",true)))) }
    @Test fun permiteCancelarMasDeVeinticuatroHoras() { assertTrue(reglas.puedeCancelar(cita("2026-09-23T15:01"))) }
    @Test fun noPermiteCancelarExactamenteVeinticuatroHoras() { assertFalse(reglas.puedeCancelar(cita("2026-09-23T15:00"))) }
    @Test fun noPermiteCancelarMenosDeVeinticuatroHoras() { assertFalse(reglas.puedeCancelar(cita("2026-09-23T14:59"))) }
    @Test fun noCancelaAtendida() { assertFalse(reglas.puedeCancelar(cita().copy(estado=EstadoCita.Atendida("Control")))) }
    @Test fun noCancelaCancelada() { assertFalse(reglas.puedeCancelar(cita().copy(estado=EstadoCita.Cancelada("Viaje",true)))) }
    @Test fun buscaSinTildesNiMayusculas() { assertEquals(1,filtrarCitas(listOf(cita()),"Todas","IVAN").size) }
    @Test fun combinaBusquedaYEstado() { assertTrue(filtrarCitas(listOf(cita()),"Atendida","ivan").isEmpty()) }
    @Test fun ordenaCronologicamente() {
        val temprano=cita("2026-09-24T09:00");val tarde=cita("2026-09-24T15:00")
        assertEquals(listOf(temprano,tarde),filtrarCitas(listOf(tarde,temprano),"Todas",""))
    }
    @Test fun semillasCumplenMinimos() {
        val citas=CitasSimuladas.citas(reloj)
        assertEquals(6,citas.size)
        assertEquals(3,citas.count { it.estado is EstadoCita.Programada })
        assertEquals(2,citas.count { it.estado is EstadoCita.Atendida })
        assertEquals(1,citas.count { it.estado is EstadoCita.Cancelada })
        assertEquals(4,CitasSimuladas.sedes.size)
        assertEquals(5,CitasSimuladas.especialidades.size)
        CitasSimuladas.especialidades.forEach { especialidad -> assertTrue(CitasSimuladas.medicos.count { it.especialidad==especialidad }>=2) }
        assertTrue(citas.filter { it.estado is EstadoCita.Programada }.all { it.fechaHora.toInstant(reloj.zona)>reloj.ahora() })
    }
    @Test fun fuenteCargaEnOchocientosMilisegundos() = runTest {
        val repo=CitaRepositoryFake(reloj)
        val inicio=testScheduler.currentTime
        repo.obtener()
        assertEquals(800L,testScheduler.currentTime-inicio)
    }
    @Test fun cancelarLiberaUnaPlazaYRegistrarActualizaDatos() = runTest {
        val repo=CitaRepositoryFake(reloj);val operaciones=OperacionesCitas()
        CancelarCitaUseCase(repo,reglas,operaciones)(1)
        val nueva=SolicitarCitaUseCase(repo,reglas,operaciones)("Medicina General","N","2026-10-01","10:00","Consulta general")
        assertEquals(7,nueva.id)
        assertEquals(3,repo.obtener().count { it.estado is EstadoCita.Programada })
        assertEquals(2,repo.cambios.value)
    }
    @Test fun solicitudesSimultaneasNoSuperanLimite() = runTest {
        val repo=CitaRepositoryFake(reloj);val operaciones=OperacionesCitas()
        CancelarCitaUseCase(repo,reglas,operaciones)(1)
        val solicitar=SolicitarCitaUseCase(repo,reglas,operaciones)
        val uno=async { runCatching { solicitar("Medicina General","N","2026-10-01","10:00","Consulta general") } }
        val dos=async { runCatching { solicitar("Medicina General","N","2026-10-02","10:00","Consulta general") } }
        assertEquals(1,listOf(uno.await(),dos.await()).count { it.isSuccess })
        assertEquals(3,repo.obtener().count { it.estado is EstadoCita.Programada })
    }
    @Test fun camposInvalidosSeInformanPorSeparado() = runTest {
        val repo=CitaRepositoryFake(reloj)
        val error=assertFailsWith<ErrorCampos> { SolicitarCitaUseCase(repo,reglas,OperacionesCitas())("","","fecha","hora","") }
        assertEquals(setOf("especialidad","sede","fecha","hora","motivo"),error.campos.keys)
    }
}
