@echo off
REM ============================================
REM  Run tests for core-service (Flash Sale Engine)
REM  Opens a NEW CMD window so test output stays visible
REM  Usage:
REM    run-tests.bat                          -> run all tests
REM    run-tests.bat InventoryConcurrencyTest -> run a specific test class
REM    run-tests.bat InventoryConcurrencyTest#methodName -> run a specific method
REM ============================================

REM Re-invoked internally: this branch actually runs the tests in the new window
if /i "%~1"=="__RUN__" goto :run

setlocal

REM Test class passed as first argument (optional)
set "TEST_CLASS=%~1"

echo Opening a new CMD window to run tests...
start "Flash Sale Engine - Tests" cmd /k ""%~f0" __RUN__ %TEST_CLASS%"

endlocal
exit /b 0

:run
REM Use UTF-8 code page so Vietnamese text displays correctly
chcp 65001 >nul
REM Force every JVM (Maven + Surefire fork) to write UTF-8 output
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"

cd /d "%~dp0src\core-service"

if "%~2"=="" (
    echo Running ALL tests...
    call mvnw.cmd test
) else (
    echo Running test: %~2
    call mvnw.cmd test -Dtest=%~2
)

echo.
echo [DONE] Test run finished.
pause
