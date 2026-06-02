@echo off
echo Setting JAVA_HOME to D:\JDK21...
set JAVA_HOME=D:\JDK21
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting Maven build...
mvn clean package -q

if %ERRORLEVEL% equ 0 (
    echo.
    echo Build successful!
    echo WAR file: target\powerbank.war
) else (
    echo.
    echo Build failed!
    pause
)