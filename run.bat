@echo off
echo Starting EzyEnglish Authentication Service...
echo Building the project with Maven...
call mvn clean install -DskipTests
if %errorlevel% equ 0 (
    echo Build successful! Starting the application on port 8082...
    call mvn spring-boot:run
) else (
    echo Build failed. Please check the errors above.
    exit /b 1
)
