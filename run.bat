@echo off
setlocal
if "%1"=="test" (
    echo =======================================================
    echo Executing Automated DSA Verification Suite...
    echo =======================================================
    if not exist "bin" mkdir bin
    javac -encoding UTF-8 -d bin src/railway/*.java
    if %ERRORLEVEL% EQU 0 (
        java -cp bin railway.DSATestSuite
    )
    pause
    exit /b %ERRORLEVEL%
)

echo =======================================================
echo Launching Railway Management System...
echo =======================================================
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin src/railway/*.java
if %ERRORLEVEL% EQU 0 (
    start "Railway Management System" java -cp bin railway.RailwayManagementSystem
) else (
    echo [ERROR] Compilation failed.
    pause
)
