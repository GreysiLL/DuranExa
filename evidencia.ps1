$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
Write-Host 'git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud --no-pager shortlog -sne main'
git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud --no-pager shortlog -sne main
Write-Host 'git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud --no-pager log --graph --oneline --all --decorate -15'
git -c safe.directory=C:/Users/HECTOR/AndroidStudioProjects/AndinaSalud --no-pager log --graph --oneline --all --decorate -15
