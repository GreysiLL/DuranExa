package pe.upeu.andinasalud.domain.model

data class Catalogo(val paciente: Paciente, val sedes: List<Sede>, val especialidades: List<String>, val medicos: List<Medico>)
