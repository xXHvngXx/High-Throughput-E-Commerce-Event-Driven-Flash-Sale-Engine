@echo off
REM ============================================
REM  Run tests for core-service (Flash Sale Engine)
REM  No error checking: script always exits 0
REM  Usage:
REM    run-tests.bat                          -> run all tests
REM    run-tests.bat InventoryConcurrencyTest -> run a specific test class
REM    run-tests.bat InventoryConcurrencyTest#methodName -> run a specific method
REM ============================================

setlocal

REM Path to core-service relative to this script's location
set "SERVICE_DIR=%~dp0src\core-service"

REM Test class passed as first argument (optional)
set "TEST_CLASS=%~1"

echo ============================================
echo  Flash Sale Engine - Running tests
echo  Directory: %SERVICE_DIR%
echo ============================================
echo.

cd /d "%SERVICE_DIR%"

if "%TEST_CLASS%"=="" (
    echo Running ALL tests...
    call mvnw.cmd test
) else (
    echo Running test: %TEST_CLASS%
    call mvnw.cmd test -Dtest=%TEST_CLASS%
)

echo.
echo [DONE] Test run finished.

endlocal & exit /b 0
