package pe.upeu.andinasalud.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
@Composable actual fun AtrasSistema(habilitado: Boolean,volver:()->Unit) { BackHandler(enabled=habilitado,onBack=volver) }
