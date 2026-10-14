@echo off
REM ============================================
REM  Run Spring Boot app for core-service (Flash Sale Engine)
REM  Starts Docker (PostgreSQL + Redis) then opens a NEW CMD window running the app
REM  Usage:
REM    run-app.bat
REM ============================================

REM Re-invoked internally: this branch actually runs the app in the new window
if /i "%~1"=="__RUN__" goto :run

cd /d "%~dp0"

echo [1/2] Starting Docker containers (PostgreSQL + Redis)...
docker compose up -d

echo.
echo [2/2] Opening a new CMD window to run the app...
start "Flash Sale Engine - App" cmd /k ""%~f0" __RUN__"

exit /b 0

:run
REM Use UTF-8 code page so Vietnamese text displays correctly
chcp 65001 >nul
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"

cd /d "%~dp0src\core-service"
call mvnw.cmd spring-boot:run

echo.
echo [DONE] App stopped.
pause