$ErrorActionPreference='Stop'
$p=$PSScriptRoot
function G { & git -c "safe.directory=$p" -C $p @args; if($LASTEXITCODE -ne 0){throw 'Git no pudo completar la operacion.'} }
if((G branch --show-current).Trim() -ne 'feature/base-andinasalud-duran'){throw 'Selecciona feature/base-andinasalud-duran.'}
G add -- shared/src androidApp/src/debug androidApp/build.gradle.kts docs README.md guardar-pruebas.ps1
G commit -m 'test(andinasalud): verificar flujos y documentar evidencias Android'
G status --short --branch
