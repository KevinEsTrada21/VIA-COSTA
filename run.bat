@echo off
chcp 65001 > nul
echo ==============================================================================
echo   VIACOSTA - SISTEMA DE VENTA DE PASAJES Y TRANSPORTE INTERPROVINCIAL
echo   Curso Integrador I: Sistemas Software (Avance 3 - UTP)
echo ==============================================================================
echo.

IF EXIST "C:\Program Files\Java\jdk-17" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    set "PATH=C:\Program Files\Java\jdk-17\bin;%PATH%"
)

echo [INFO] Utilizando Java: %JAVA_HOME%
echo [INFO] Iniciando compilacion y ejecucion JavaFX con Maven Wrapper...
echo.

call .\mvnw.cmd compile javafx:run

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Se produjo un error al compilar o ejecutar el proyecto.
    pause
)
