package pe.upeu.andinasalud.presentation.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.solicitud.*
import pe.upeu.andinasalud.presentation.inicio.InicioScreen
import pe.upeu.andinasalud.presentation.perfil.*
@Composable expect fun AtrasSistema(habilitado: Boolean,volver:()->Unit)
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AppNavHost(oscuro: Boolean,cambiarTema:(Boolean)->Unit) {
 var principal by rememberSaveable { mutableStateOf(Destinos.INICIO) }
 var ruta by rememberSaveable { mutableStateOf("") }
 var citaId by rememberSaveable { mutableStateOf(0) }
 var origenDetalle by rememberSaveable { mutableStateOf("") }
 var sesionSolicitud by rememberSaveable { mutableStateOf(0) }
 val vm: CitasViewModel=koinViewModel()
 val s by vm.estado.collectAsState()
 val volver:()->Unit={ if(ruta==Destinos.DETALLE && origenDetalle==Destinos.SOLICITUD){ruta=Destinos.SOLICITUD;origenDetalle=""}else{ruta=""} }
 AtrasSistema(ruta.isNotEmpty() || principal!=Destinos.INICIO) { if(ruta.isNotEmpty())volver() else principal=Destinos.INICIO }
 val abrirDetalle:(Int)->Unit={origenDetalle=ruta;citaId=it;ruta=Destinos.DETALLE}
 val solicitar:()->Unit={sesionSolicitud++;ruta=Destinos.SOLICITUD}
 Scaffold(topBar={TopAppBar(title={Text(if(ruta.isEmpty())principal else ruta)},navigationIcon={if(ruta.isNotEmpty())TextButton(onClick=volver){Text("Atrás")}})},bottomBar={
 NavigationBar { listOf(Destinos.INICIO,Destinos.CITAS,Destinos.PERFIL).forEachIndexed { i,d->
 NavigationBarItem(selected=principal==d,onClick={principal=d;ruta=""},icon={Text(listOf("⌂","▤","●")[i])},label={Text(d)})
 } } }) { padding -> Box(Modifier.fillMaxSize().padding(padding)) {
 when(ruta.ifEmpty { principal }) {
 Destinos.INICIO->InicioScreen(s,vm::cargar,{principal=Destinos.CITAS},solicitar,abrirDetalle)
 Destinos.CITAS->CitasScreen(s,vm.visibles(s),vm::buscar,vm::filtrar,vm::cargar,solicitar,abrirDetalle)
 Destinos.PERFIL->PerfilScreen(s,vm::cargar){ruta=Destinos.AJUSTES}
 Destinos.AJUSTES->AjustesScreen(oscuro,cambiarTema)
 Destinos.DETALLE->{ val detalle: DetalleCitaViewModel=koinViewModel();val e by detalle.estado.collectAsState();LaunchedEffect(citaId){detalle.cargar(citaId)};DetalleCitaScreen(e,{detalle.cargar(citaId)},detalle::cancelar) }
 Destinos.SOLICITUD->{ val form: SolicitudViewModel=koinViewModel(key="solicitud-$sesionSolicitud");val e by form.estado.collectAsState();SolicitudScreen(e,form::cargar,form::cambiar,form::registrar,abrirDetalle) }
 }
 } }
}
