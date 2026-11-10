@echo off
REM ============================================================
REM  Build script: locks JDK 21 for this window only.
REM  Usage:
REM     build.cmd clean package
REM     build.cmd spring-boot:run
REM  Note: keep this file ASCII-only. cmd.exe reads .cmd files
REM        with the system ANSI code page; Chinese text here
REM        would be garbled and executed as commands.
REM ============================================================
setlocal
chcp 65001 >nul 2>nul

if exist "D:\java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=D:\java\jdk-21"
    set "PATH=D:\java\jdk-21\bin;%PATH%"
) else (
    echo [build] WARN: JDK 21 not found at D:\java\jdk-21, using default JDK
)

echo [build] JAVA_HOME=%JAVA_HOME%

set "MVN_CMD="
if exist "D:\tools\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MVN_CMD=D:\tools\apache-maven-3.9.9\bin\mvn.cmd"
) else (
    where mvn >nul 2>nul && set "MVN_CMD=mvn"
)

if "%MVN_CMD%"=="" (
    echo [build] ERROR: Maven not found. Install Maven or build in IDEA.
    exit /b 1
)

pushd "%~dp0backend"
call "%MVN_CMD%" %*
set "EXIT_CODE=%ERRORLEVEL%"
popd

exit /b %EXIT_CODE%
