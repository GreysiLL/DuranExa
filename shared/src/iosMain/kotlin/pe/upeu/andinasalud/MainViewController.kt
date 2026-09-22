package pe.upeu.andinasalud
import androidx.compose.ui.window.ComposeUIViewController
import pe.upeu.andinasalud.di.initKoin
private object InicioIos { init { initKoin() }; fun preparar() {} }
fun MainViewController() = run { InicioIos.preparar(); ComposeUIViewController { App() } }
