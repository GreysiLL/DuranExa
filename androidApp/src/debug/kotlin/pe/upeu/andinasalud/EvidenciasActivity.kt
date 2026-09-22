package pe.upeu.andinasalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.citas.CitasUiState
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

/** Banco visual de prueba: solo existe en debug, no en la aplicacion release.
 * Inyecta estados en la pantalla real. No simula una consulta real ni modifica datos.
 * El recorrido repositorio-error-reintento se verifica en EstadosPantallaTest.
 */
class EvidenciasActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val escenario = intent.getStringExtra("escenario") ?: "error"
        setContent {
            AndinaSaludTheme(false) {
                var estado by remember {
                    mutableStateOf(CitasUiState(
                        cargando = escenario == "carga",
                        error = if (escenario == "error") "No se pudieron cargar las citas. Error simulado para pruebas." else null
                    ))
                }
                Scaffold(topBar = { TopAppBar(title = { Text("Citas · prueba visual") }) }) { espacio ->
                    Box(Modifier.padding(espacio)) {
                        CitasScreen(estado, emptyList(), {}, {},
                            reintentar = { estado = CitasUiState(cargando=false) },
                            solicitar = {}, detalle = {})
                    }
                }
            }
        }
    }
}
