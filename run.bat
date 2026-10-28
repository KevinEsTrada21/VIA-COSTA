@echo off
chcp 65001 > nul
echo ==============================================================================
echo   VIACOSTA - SISTEMA DE VENTA DE PASAJES Y TRANSPORTE INTERPROVINCIAL
echo   Curso Integrador I: Sistemas Software (Java Swing - NetBeans GUI)
echo ==============================================================================
echo.

FOR /D %%i IN ("C:\Program Files\Java\jdk-*") DO (
    set "JAVA_HOME=%%i"
)
IF DEFINED JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

echo [INFO] Utilizando Java: %JAVA_HOME%
echo [INFO] Iniciando compilacion y ejecucion Java Swing...
echo.

call .\mvnw.cmd compile exec:java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Se produjo un error al compilar o ejecutar el proyecto.
    pause
)
