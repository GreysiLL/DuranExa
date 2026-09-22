package pe.upeu.andinasalud.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun Context.actividad(): Activity? = when(this) {
    is Activity -> this
    is ContextWrapper -> baseContext.actividad()
    else -> null
}

@Composable actual fun BarrasSistema(oscuro: Boolean) {
    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            vista.context.actividad()?.let { actividad ->
                WindowCompat.getInsetsController(actividad.window, vista).apply {
                    isAppearanceLightStatusBars = !oscuro
                    isAppearanceLightNavigationBars = !oscuro
                }
            }
        }
    }
}
