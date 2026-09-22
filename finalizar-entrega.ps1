$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$safePath=$PSScriptRoot.Replace('\','/')
function Git-Final {
    & git -c "safe.directory=$safePath" @args
    if ($LASTEXITCODE -ne 0) { throw 'Git no completo la operacion. No se ha confirmado la entrega final.' }
}
$remote='entrega'
$branch='fix/entrega-final-duran'
$tag='v1.0-unidad1'
if ((Git-Final remote get-url $remote) -ne 'https://github.com/GreysiLL/DuranExa.git') { throw 'El remoto no corresponde a DuranExa.' }
Git-Final fetch $remote --tags
foreach ($name in @('main','develop')) {
    & git -c "safe.directory=$safePath" merge-base --is-ancestor "${remote}/$name" $name
    if ($LASTEXITCODE -ne 0) { throw "Hay cambios remotos pendientes en $name. Se detuvo sin reemplazarlos." }
}
$current=Git-Final branch --show-current
if ($current -notin @('sc-abcd-duran',$branch)) { throw 'Ejecuta desde sc-abcd-duran o fix/entrega-final-duran.' }
$files=@(Git-Final diff --name-only HEAD) + @(Git-Final ls-files --others --exclude-standard)
$unexpected=@($files | Where-Object { $_ -and $_ -notmatch '^(\.gitattributes$|docs/|README\.md$|finalizar-entrega\.ps1$|shared/src/commonMain/kotlin/pe/upeu/andinasalud/data/local/CitasSimuladas\.kt$|shared/src/commonMain/kotlin/pe/upeu/andinasalud/presentation/detalle/DetalleCitaViewModel\.kt$|shared/src/commonTest/kotlin/pe/upeu/andinasalud/EstadosPantallaTest\.kt$)' })
if ($unexpected.Count) { $unexpected | Write-Host;throw 'Hay cambios adicionales que deben revisarse antes de publicar.' }
if ($current -ne $branch) {
    if (Git-Final branch --list $branch) { throw 'La rama final ya existe. Cambia a ella antes de continuar.' }
    Git-Final switch -c $branch develop
}
Git-Final add -- .gitattributes README.md docs finalizar-entrega.ps1 shared/src/commonMain/kotlin/pe/upeu/andinasalud/data/local/CitasSimuladas.kt shared/src/commonMain/kotlin/pe/upeu/andinasalud/presentation/detalle/DetalleCitaViewModel.kt shared/src/commonTest/kotlin/pe/upeu/andinasalud/EstadosPantallaTest.kt
& git -c "safe.directory=$safePath" diff --cached --quiet
if ($LASTEXITCODE -eq 1) { Git-Final commit -m 'fix(entrega): recuperar errores del detalle y verificar entrega Android' }
elseif ($LASTEXITCODE -ne 0) { throw 'No se pudo revisar el contenido preparado.' }
Git-Final switch develop
Git-Final merge --no-ff $branch -m 'merge: integrar evidencias finales de Android en develop'
Git-Final switch main
Git-Final merge --no-ff develop -m 'merge: cerrar entrega Android de la Unidad 1'
$head=Git-Final rev-parse HEAD
if (Git-Final tag --list $tag) {
    $old=Git-Final rev-parse "$tag^{}"
    if ($old -ne $head) { throw 'La etiqueta ya apunta a otra version. No se reemplazo.' }
} else { Git-Final tag -a $tag -m 'Entrega Unidad 1 - AndinaSalud Android; modalidad autorizada informada por la estudiante' }
Git-Final switch $branch
Git-Final push --atomic -u $remote $branch develop main "refs/tags/$tag"
$remoteMain=Git-Final ls-remote $remote refs/heads/main
$remoteTag=Git-Final ls-remote $remote "refs/tags/$tag^{}"
if (($remoteMain -split '\s+')[0] -ne $head -or ($remoteTag -split '\s+')[0] -ne $head) { throw 'No se pudo verificar main y la etiqueta en GitHub.' }
Write-Host ''
Write-Host 'ENTREGA FINAL PUBLICADA Y VERIFICADA'
Write-Host 'https://github.com/GreysiLL/DuranExa/tree/v1.0-unidad1'
Write-Host 'La etiqueta apunta al commit final. No se reescribio el historial.'
Write-Host 'Los requisitos historicos de commits y revisiones siguen sujetos al criterio del profesor.'
