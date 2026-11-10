@echo off
REM ============================================================
REM  构建脚本：只在当前窗口临时锁定 JDK 21，不改系统环境变量
REM  用法:
REM     build.cmd clean package
REM     build.cmd spring-boot:run
REM  说明:
REM     - 若 D:\java\jdk-21 不存在，会退回系统默认 JDK
REM     - Maven 找不到时，请在 IDEA 里用自带的 Maven 窗口构建
REM ============================================================
setlocal

if exist "D:\java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=D:\java\jdk-21"
    set "PATH=D:\java\jdk-21\bin;%PATH%"
    echo [build] JAVA_HOME=%JAVA_HOME%
) else (
    echo [build] 警告: 未找到 D:\java\jdk-21，使用系统默认 JDK
)

set "MVN_CMD="
if exist "D:\tools\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MVN_CMD=D:\tools\apache-maven-3.9.9\bin\mvn.cmd"
) else (
    where mvn >nul 2>nul && set "MVN_CMD=mvn"
)

if "%MVN_CMD%"=="" (
    echo [build] 错误: 未找到 Maven，请先安装或使用 IDEA 自带 Maven
    exit /b 1
)

REM 统一在 backend 目录执行
pushd "%~dp0backend"
call "%MVN_CMD%" %*
set "EXIT_CODE=%ERRORLEVEL%"
popd

exit /b %EXIT_CODE%
