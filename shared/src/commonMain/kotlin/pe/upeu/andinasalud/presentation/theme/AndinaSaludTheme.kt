package pe.upeu.andinasalud.presentation.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val claro=lightColorScheme(primary=AzulAndina,secondary=AmbarAndina,primaryContainer=Color(0xFFD2E8F7),onPrimaryContainer=Color(0xFF082F47),background=Color(0xFFFAF9F5),surface=Color(0xFFFAF9F5))
private val oscuro=darkColorScheme(primary=Color(0xFF98CFEE),secondary=Color(0xFFF1CC72),primaryContainer=Color(0xFF163E55),onPrimaryContainer=Color(0xFFD2E8F7),background=Color(0xFF101820),surface=Color(0xFF101820))
@Composable fun AndinaSaludTheme(oscuroActivo: Boolean,contenido: @Composable ()->Unit) {
 MaterialTheme(colorScheme=if(oscuroActivo) oscuro else claro,typography=TipografiaAndina,content=contenido)
}
