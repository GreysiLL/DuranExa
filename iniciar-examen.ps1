$ErrorActionPreference='Stop'
$p=$PSScriptRoot
function G { & git -c "safe.directory=$p" -C $p @args; if($LASTEXITCODE -ne 0){throw 'Git no pudo completar la operacion.'} }
G add -- .
G commit -m 'chore: inicializar la plantilla KMP de Android Studio'
G switch -c develop
G switch -c feature/base-andinasalud-duran
G status --short --branch
