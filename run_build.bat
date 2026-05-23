@echo off
cd /d "c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse"
echo ================================================================================
echo                     CivicPulse Build Process
echo ================================================================================
echo.
echo Starting Maven build at %date% %time%
echo.
.\mvnw.cmd clean package -DskipTests
echo.
echo Build completed at %date% %time%
pause
