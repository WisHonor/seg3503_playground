@echo off

del /Q dist\*.class 2>nul

javac -encoding UTF-8 --source-path src -d dist src\*.java
if errorlevel 1 exit /b 1

javac -encoding UTF-8 --source-path test -d dist -cp "dist;lib\junit-platform-console-standalone-1.7.1.jar" test\*.java
if errorlevel 1 exit /b 1

java -jar lib\junit-platform-console-standalone-1.7.1.jar --class-path dist --scan-class-path
