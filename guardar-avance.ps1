$ErrorActionPreference = 'Stop'
$proyecto = $PSScriptRoot
function G {
    & git -c "safe.directory=$proyecto" -C $proyecto @args
    if ($LASTEXITCODE -ne 0) { throw 'Git no pudo completar la operacion.' }
}
if ((G branch --show-current).Trim() -ne 'feature/base-andinasalud-duran') {
    throw 'Selecciona la rama feature/base-andinasalud-duran.'
}
G add -- README.md androidApp/src/main/AndroidManifest.xml androidApp/src/main/kotlin/pe/upeu/andinasalud/MainApplication.kt shared/build.gradle.kts shared/src docs/VERIFICACION.md guardar-avance.ps1
G commit -m 'feat(andinasalud): implementar citas en memoria con reglas y pantallas'
G status --short --branch
