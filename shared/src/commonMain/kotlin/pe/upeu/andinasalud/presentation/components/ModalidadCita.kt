package pe.upeu.andinasalud.presentation.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

@Composable fun ModalidadCita(modalidad: ModalidadAtencion) {
    val color=MaterialTheme.colorScheme.primary
    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        // Dos iconos vectoriales simples: persona para presencial y cámara para teleconsulta.
        Canvas(Modifier.size(22.dp)) {
            val u=size.width/24
            if(modalidad==ModalidadAtencion.PRESENCIAL) {
                drawCircle(color,4*u,Offset(12*u,6*u),style=Stroke(2*u))
                drawArc(color,180f,180f,false,Offset(4*u,13*u),Size(16*u,15*u),style=Stroke(2*u))
            } else {
                drawRect(color,Offset(2*u,6*u),Size(13*u,12*u),style=Stroke(2*u))
                drawPath(Path().apply { moveTo(15*u,10*u);lineTo(22*u,6*u);lineTo(22*u,18*u);lineTo(15*u,14*u);close() },color)
            }
        }
        Text(modalidad.nombre)
    }
}
