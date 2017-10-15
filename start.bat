@echo off
cd /d  "%~dp0"
cmd /k for /r %%i in ("Spaventapasseri\app\src\main\java\brainstorm\spaventapasseri\*.java") do style "%%i"


