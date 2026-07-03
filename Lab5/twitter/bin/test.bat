@echo off

if not exist dist mkdir dist

del /Q dist\*.class 2>nul

javac -encoding UTF-8 --source-path src -d dist src\*.java
if errorlevel 1 exit /b 1

javac -encoding UTF-8 --source-path test -d dist -cp "dist;lib\easymock-4.3.jar;lib\junit-platform-console-standalone-1.7.1.jar" test\*.java
if errorlevel 1 exit /b 1

java --add-opens java.base/java.lang=ALL-UNNAMED -jar lib\junit-platform-console-standalone-1.7.1.jar -cp "dist;lib\easymock-4.3.jar;lib\objenesis-3.2.jar" --scan-class-path
