$ErrorActionPreference = 'Stop'

$projectRoot = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$outputDirectory = Join-Path $projectRoot 'out\artifacts'
$buildId = [guid]::NewGuid().ToString('N')
$workspaceDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ('SimulatorBuild-' + $buildId)
$iexpressWorkspaceDirectory = Join-Path $projectRoot 'out\portable'
$iexpressSourceDirectory = Join-Path $iexpressWorkspaceDirectory 'input'
$shortBuildId = $buildId.Substring(0, 8)
$workPortableExe = Join-Path $outputDirectory ('Simulator-' + $shortBuildId + '.exe')
$workspaceFullPath = [System.IO.Path]::GetFullPath($workspaceDirectory).TrimEnd('\')
$tempRootFullPath = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath()).TrimEnd('\') + '\'
$buildSucceeded = $false

try {
    foreach ($toolName in @('javac.exe', 'jar.exe', 'jpackage.exe')) {
        if (-not (Get-Command $toolName -ErrorAction SilentlyContinue)) {
            throw "Could not find $toolName. Install a JDK and add its bin directory to PATH."
        }
    }

    $iexpressPath = Join-Path $env:WINDIR 'System32\iexpress.exe'
    if (-not (Test-Path -LiteralPath $iexpressPath)) {
        throw 'IExpress was not found. It is required to create the single-file portable executable.'
    }

    $sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Filter '*.java' -Recurse |
        Select-Object -ExpandProperty FullName)
    if ($sourceFiles.Count -eq 0) {
        throw 'No Java source files were found under src.'
    }

    $classesDirectory = Join-Path $workspaceDirectory 'classes'
    $jpackageInputDirectory = Join-Path $workspaceDirectory 'jpackage-input'
    $jpackageOutputDirectory = Join-Path $workspaceDirectory 'jpackage-output'
    $iexpressInputDirectory = Join-Path $workspaceDirectory 'iexpress-input'
    New-Item -ItemType Directory -Force -Path @(
        $classesDirectory,
        $jpackageInputDirectory,
        $jpackageOutputDirectory,
        $iexpressInputDirectory,
        $iexpressSourceDirectory,
        $outputDirectory
    ) | Out-Null

    Write-Host 'Compiling Java sources...'
    & javac.exe -d $classesDirectory @sourceFiles
    if ($LASTEXITCODE -ne 0) {
        throw "javac failed with exit code $LASTEXITCODE."
    }

    $workJar = Join-Path $workspaceDirectory 'Simulator.jar'
    Write-Host 'Creating executable JAR...'
    & jar.exe --create --file $workJar --main-class Main -C $classesDirectory .
    if ($LASTEXITCODE -ne 0) {
        throw "jar failed with exit code $LASTEXITCODE."
    }

    Copy-Item -LiteralPath $workJar -Destination (Join-Path $jpackageInputDirectory 'Simulator.jar')

    Write-Host 'Bundling the Java runtime...'
    & jpackage.exe --type app-image --name Simulator `
        --input $jpackageInputDirectory `
        --main-jar Simulator.jar `
        --main-class Main `
        --dest $jpackageOutputDirectory
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage failed with exit code $LASTEXITCODE."
    }

    $appImageDirectory = Join-Path $jpackageOutputDirectory 'Simulator'
    $appLauncher = Join-Path $appImageDirectory 'Simulator.exe'
    if (-not (Test-Path -LiteralPath $appLauncher)) {
        throw 'jpackage completed without creating Simulator.exe.'
    }

    $payloadArchive = Join-Path $iexpressInputDirectory 'Simulator.zip'
    Write-Host 'Compressing the portable app...'
    Compress-Archive -Path (Join-Path $appImageDirectory '*') `
        -DestinationPath $payloadArchive `
        -CompressionLevel Optimal

    @'
@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -WindowStyle Hidden -File "%~dp0launch.ps1"
'@ | Set-Content -LiteralPath (Join-Path $iexpressInputDirectory 'launch.cmd') -Encoding Ascii

    @'
$ErrorActionPreference = 'Stop'
$destination = Join-Path $env:TEMP ('Simulator-Portable-' + [guid]::NewGuid().ToString('N'))
try {
    New-Item -ItemType Directory -Path $destination | Out-Null
    Expand-Archive -LiteralPath (Join-Path $PSScriptRoot 'Simulator.zip') -DestinationPath $destination
    $app = Start-Process -FilePath (Join-Path $destination 'Simulator.exe') -WorkingDirectory $destination -PassThru -Wait
    exit $app.ExitCode
}
finally {
    if (Test-Path -LiteralPath $destination) {
        Remove-Item -LiteralPath $destination -Recurse -Force
    }
}
'@ | Set-Content -LiteralPath (Join-Path $iexpressInputDirectory 'launch.ps1') -Encoding Ascii

    Copy-Item -LiteralPath (Join-Path $iexpressInputDirectory 'Simulator.zip') -Destination $iexpressSourceDirectory -Force
    Copy-Item -LiteralPath (Join-Path $iexpressInputDirectory 'launch.cmd') -Destination $iexpressSourceDirectory -Force
    Copy-Item -LiteralPath (Join-Path $iexpressInputDirectory 'launch.ps1') -Destination $iexpressSourceDirectory -Force

    $sedPath = Join-Path $iexpressWorkspaceDirectory 'Simulator-Portable.sed'
    $sedContent = @"
[Version]
Class=IEXPRESS
SEDVersion=3

[Options]
PackagePurpose=InstallApp
ShowInstallProgramWindow=0
HideExtractAnimation=1
UseLongFileName=1
InsideCompressed=1
CAB_FixedSize=0
CAB_ResvCodeSigning=0
RebootMode=N
InstallPrompt=
DisplayLicense=
FinishMessage=
TargetName=$workPortableExe
FriendlyName=Simulator Portable
AppLaunched=cmd.exe /c launch.cmd
PostInstallCmd=<None>
AdminQuietInstCmd=
UserQuietInstCmd=
SourceFiles=SourceFiles

[Strings]
InstallPrompt=
DisplayLicense=
FinishMessage=
FriendlyName=Simulator Portable
PostInstallCmd=<None>
AdminQuietInstCmd=
UserQuietInstCmd=
FILE0="Simulator.zip"
FILE1="launch.cmd"
FILE2="launch.ps1"

[SourceFiles]
SourceFiles0=$iexpressSourceDirectory

[SourceFiles0]
%FILE0%=
%FILE1%=
%FILE2%=
"@
    Set-Content -LiteralPath $sedPath -Value $sedContent -Encoding Ascii

    Write-Host 'Creating the single-file portable executable...'
    $iexpressProcess = Start-Process -FilePath $iexpressPath `
        -ArgumentList @('/N', '/Q', $sedPath) `
        -WorkingDirectory $iexpressWorkspaceDirectory `
        -Wait `
        -PassThru
    if ($iexpressProcess.ExitCode -ne 0 -or -not (Test-Path -LiteralPath $workPortableExe)) {
        throw "IExpress failed to create the portable executable (exit code $($iexpressProcess.ExitCode))."
    }

    $finalJar = Join-Path $outputDirectory 'Simulator.jar'
    $finalPortableExe = Join-Path $outputDirectory 'Simulator-Portable.exe'
    Move-Item -LiteralPath $workJar -Destination $finalJar -Force
    Move-Item -LiteralPath $workPortableExe -Destination $finalPortableExe -Force
    $buildSucceeded = $true

    Write-Host ''
    Write-Host 'Build complete:'
    Write-Host "  JAR:      $finalJar"
    Write-Host "  Portable: $finalPortableExe"
    Write-Host 'The portable EXE extracts the bundled app to a temporary directory when launched.'
}
finally {
    if ($buildSucceeded) {
        $stagingFiles = @(
            (Join-Path $iexpressSourceDirectory 'Simulator.zip'),
            (Join-Path $iexpressSourceDirectory 'launch.cmd'),
            (Join-Path $iexpressSourceDirectory 'launch.ps1'),
            (Join-Path $iexpressWorkspaceDirectory 'Simulator-Portable.sed'),
            (Join-Path $iexpressWorkspaceDirectory ('~Simulator-' + $shortBuildId + '.DDF')),
            (Join-Path $iexpressWorkspaceDirectory ('~Simulator-' + $shortBuildId + '.CAB'))
        )
        foreach ($stagingFile in $stagingFiles) {
            if (Test-Path -LiteralPath $stagingFile -PathType Leaf) {
                Remove-Item -LiteralPath $stagingFile -Force
            }
        }
    }

    $workspaceWithSeparator = $workspaceFullPath + '\'
    if ($buildSucceeded -and
        $workspaceWithSeparator.StartsWith($tempRootFullPath, [System.StringComparison]::OrdinalIgnoreCase) -and
        (Split-Path -Leaf $workspaceFullPath).StartsWith('SimulatorBuild-', [System.StringComparison]::OrdinalIgnoreCase) -and
        (Test-Path -LiteralPath $workspaceFullPath)) {
        Remove-Item -LiteralPath $workspaceFullPath -Recurse -Force
    }

    if (-not $buildSucceeded) {
        if (Test-Path -LiteralPath $workspaceFullPath) {
            Write-Warning "Build files were kept for diagnosis at $workspaceFullPath"
        }
    }
}
