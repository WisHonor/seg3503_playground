$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath "dist")) {
  New-Item -ItemType Directory -Path "dist" | Out-Null
}

Remove-Item -Path "dist\*.class" -Force -ErrorAction SilentlyContinue

javac -encoding UTF-8 --source-path src -d dist src\*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

javac -encoding UTF-8 --source-path test -d dist -cp "dist;lib\easymock-4.3.jar;lib\junit-platform-console-standalone-1.7.1.jar" test\*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java --add-opens java.base/java.lang=ALL-UNNAMED -jar lib\junit-platform-console-standalone-1.7.1.jar -cp "dist;lib\easymock-4.3.jar;lib\objenesis-3.2.jar" --scan-class-path
exit $LASTEXITCODE
