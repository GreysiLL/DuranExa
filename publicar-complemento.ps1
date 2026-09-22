$ErrorActionPreference='Stop'
Set-Location -LiteralPath $PSScriptRoot
function G {
  & git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud @args
  if($LASTEXITCODE -ne 0){throw 'La operacion Git no termino. No se confirma la publicacion.'}
}
$branch='docs/evidencia-defensa-duran'
if((G remote get-url entrega) -ne 'https://github.com/GreysiLL/DuranExa.git'){throw 'Remoto inesperado'}
$allowed=@('README.md','docs/AUDITORIA-EXAMEN.md','docs/ENTREGA-FINAL.md','docs/Evidencia-Git-AndinaSalud.pdf','docs/GIT-FINAL/shortlog-terminal-real.png','docs/GUIA-DEFENSA.md','evidencia.ps1','publicar-complemento.ps1')
$changed=@(G diff --name-only HEAD)+@(G ls-files --others --exclude-standard)
if(@($changed | Where-Object {$_ -and $_ -notin $allowed}).Count){throw 'Hay cambios adicionales no incluidos en el complemento.'}
G fetch entrega
foreach($b in @('main','develop')){
  & git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud merge-base --is-ancestor "entrega/$b" $b
  if($LASTEXITCODE -ne 0){throw 'Existen cambios remotos pendientes.'}
}
$tag=G rev-parse 'v1.0-unidad1^{}'
if((G branch --show-current) -ne $branch){G switch -c $branch develop}
G add -- @allowed
& git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud diff --cached --quiet
if($LASTEXITCODE -eq 1){G commit -m 'docs(evidencia): incorporar captura real de Git y guia de defensa'}
elseif($LASTEXITCODE -ne 0){throw 'No se pudo revisar el indice'}
G switch develop
G merge --no-ff $branch -m 'merge: integrar complemento de evidencias en develop'
G switch main
G merge --no-ff develop -m 'merge: publicar complemento documental de la entrega'
$head=G rev-parse HEAD
G push --atomic -u entrega $branch develop main
$remote=G ls-remote entrega refs/heads/main
if(($remote -split '\s+')[0] -ne $head){throw 'No coincide el commit remoto'}
if((G rev-parse 'v1.0-unidad1^{}') -ne $tag){throw 'La etiqueta cambio inesperadamente'}
G switch $branch
Write-Host 'COMPLEMENTO PUBLICADO Y VERIFICADO. La etiqueta original se conserva.'
Write-Host 'https://github.com/GreysiLL/DuranExa/blob/main/docs/Evidencia-Git-AndinaSalud.pdf'
