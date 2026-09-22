$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$safePath = $PSScriptRoot.Replace('\','/')
$destination = 'https://github.com/GreysiLL/DuranExa.git'
$remoteName = 'entrega'
$workBranch = 'sc-abcd-duran'
function Run-Git {
    & git -c "safe.directory=$safePath" @args
    if ($LASTEXITCODE -ne 0) { throw 'Git no pudo completar la operacion. No se confirmo la subida.' }
}

$branch = Run-Git branch --show-current
if ($branch -ne $workBranch) { throw 'Abre la rama sc-abcd-duran antes de ejecutar este script.' }
foreach ($required in @('main','develop','feature/base-andinasalud-duran',$workBranch)) {
    Run-Git rev-parse --verify "refs/heads/$required" | Out-Null
}

# Permitir solo los archivos de entrega preparados en esta sesion.
$allowed = @('README.md','subir-andinasalud.ps1','docs/Pruebas-Funcionales-AndinaSalud-Capturas.pdf')
$tracked = @(Run-Git diff --name-only HEAD)
$untracked = @(Run-Git ls-files --others --exclude-standard)
$unexpected = @($tracked + $untracked | Where-Object { $_ -and $_ -notin $allowed })
if ($unexpected.Count -gt 0) {
    $unexpected | ForEach-Object { Write-Host "Cambio pendiente: $_" }
    throw 'Hay cambios adicionales. Guardalos en un commit antes de publicar.'
}

$remotes = @(Run-Git remote)
if ($remoteName -in $remotes) {
    $url = Run-Git remote get-url $remoteName
    if ($url -ne $destination) { throw 'El remoto entrega apunta a otro repositorio. No se modifico.' }
} else { Run-Git remote add $remoteName $destination }
Run-Git fetch $remoteName

# No reemplazar commits existentes en GitHub que no esten en las ramas locales.
foreach ($name in @('main','develop','feature/base-andinasalud-duran',$workBranch)) {
    $remoteRef = "refs/remotes/$remoteName/$name"
    & git -c "safe.directory=$safePath" show-ref --verify --quiet $remoteRef
    if ($LASTEXITCODE -eq 0) {
        & git -c "safe.directory=$safePath" merge-base --is-ancestor $remoteRef $name
        if ($LASTEXITCODE -ne 0) { throw "GitHub tiene cambios diferentes en $name. Se detuvo para conservarlos." }
    } elseif ($LASTEXITCODE -ne 1) { throw 'No se pudieron comprobar las ramas remotas.' }
}

Run-Git add -- README.md subir-andinasalud.ps1 docs/Pruebas-Funcionales-AndinaSalud-Capturas.pdf
& git -c "safe.directory=$safePath" diff --cached --quiet
if ($LASTEXITCODE -eq 1) {
    Run-Git commit -m 'docs: preparar evidencias y publicacion de AndinaSalud'
} elseif ($LASTEXITCODE -ne 0) { throw 'No se pudieron comprobar los cambios preparados.' }

Run-Git switch develop
Run-Git merge --no-ff feature/base-andinasalud-duran -m 'merge: integrar la aplicacion base en develop'
Run-Git merge --no-ff $workBranch -m 'merge: integrar los cuatro cambios de la Parte II en develop'
Run-Git switch main
Run-Git merge --no-ff develop -m 'merge: publicar AndinaSalud y la Parte II desde develop'
Run-Git switch $workBranch

# Publicacion atomica: todas las ramas, sin --force.
Run-Git push --atomic -u $remoteName main develop feature/base-andinasalud-duran $workBranch

$remoteLines = @(Run-Git ls-remote --heads $remoteName)
foreach ($name in @('main','develop','feature/base-andinasalud-duran',$workBranch)) {
    $localSha = Run-Git rev-parse $name
    $line = $remoteLines | Where-Object { ($_ -split '\s+')[1] -eq "refs/heads/$name" }
    if (-not $line -or ($line -split '\s+')[0] -ne $localSha) { throw "No se pudo verificar la rama $name en GitHub." }
}
Write-Host ''
Write-Host 'SUBIDA VERIFICADA: https://github.com/GreysiLL/DuranExa'
Write-Host 'main y develop incluyen el proyecto completo. Se conservaron las ramas de trabajo.'
Write-Host 'iOS no fue ejecutado. Las fusiones no sustituyen las revisiones requeridas por el profesor.'
