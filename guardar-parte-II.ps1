$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
function Git-Examen {
    & git -c "safe.directory=$($PSScriptRoot.Replace('\','/'))" @args
    if ($LASTEXITCODE -ne 0) { throw 'Git no pudo completar la operacion.' }
}
$branch = 'sc-abcd-duran'
$current = Git-Examen branch --show-current
if ($current -ne $branch) {
    $exists = Git-Examen branch --list $branch
    if ($exists) { throw 'La rama sc-abcd-duran ya existe. Revisa su contenido antes de cambiarte.' }
    Git-Examen switch -c $branch
}
Git-Examen add -- shared/src androidApp/src/debug androidApp/build.gradle.kts README.md docs guardar-pruebas.ps1 guardar-parte-II.ps1
& git -c "safe.directory=$($PSScriptRoot.Replace('\','/'))" diff --cached --quiet
if ($LASTEXITCODE -eq 1) {
    Git-Examen commit -m 'feat(parte-II): implementar Hoy, limite, modalidad y reprogramacion con pruebas'
} elseif ($LASTEXITCODE -ne 0) { throw 'No se pudieron revisar los cambios preparados.' }
Git-Examen status --short --branch
Write-Host 'Parte II guardada localmente. No se ha publicado el repositorio.'
