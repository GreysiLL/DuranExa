package pe.upeu.andinasalud.data.local
import kotlinx.datetime.*
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.Reloj
object CitasSimuladas {
 val paciente = Paciente("P-0417","Grease Duran Castro","70154823","grease.duran@correo.pe","910163345")
 val sedes = listOf(Sede("N","Ñaña"),Sede("C","Chosica"),Sede("CH","Chaclacayo"),Sede("S","Santa Anita"))
 val especialidades = listOf("Medicina General","Odontología","Pediatría","Nutrición","Psicología")
 val medicos = especialidades.flatMapIndexed { indice, esp ->
   val nombres = listOf("Dr. Iván Rojas","Dra. Elena Soto","Dra. Rosa Flores","Dr. Luis Pérez","Dra. Carla Núñez","Dr. José Vega","Lic. Ana Bermúdez","Lic. Pedro Ruiz","Ps. Luis Tapia","Ps. Sofía Ramos")
   listOf(Medico("M${indice*2}",nombres[indice*2],esp,listOf("N","C","CH","S")),
          Medico("M${indice*2+1}",nombres[indice*2+1],esp,listOf("N","C","CH","S")))
 }
 val catalogo = Catalogo(paciente,sedes,especialidades,medicos)
 fun citas(reloj: Reloj): List<Cita> {
   val hoy = reloj.ahora().toLocalDateTime(reloj.zona).date
   return (0..5).map { i ->
     val fecha = hoy.plus(if(i<3) i+2 else -i,DateTimeUnit.DAY)
     val estado = when(i) { 0,1,2 -> EstadoCita.Programada(i!=1); 3,4 -> EstadoCita.Atendida("Control en tres meses."); else -> EstadoCita.Cancelada("Viaje del paciente",true) }
     Cita(i+1,paciente.id,medicos[i*2 % medicos.size],sedes[i % sedes.size],LocalDateTime(fecha,LocalTime(9+i,0)),"Consulta y evaluación general",estado,
       if(i % 2 == 0) ModalidadAtencion.PRESENCIAL else ModalidadAtencion.TELECONSULTA)
   }
 }
}
