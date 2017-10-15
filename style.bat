@echo off
echo Processing + %1
Set filename=%1
For %%A in ("%filename%") do (
    Set Folder=%%~dpA
    Set Name=%%~nxA
)
del %1.bak
echo Folder: %Folder%
Echo name: %Name%
copy %1 %1.bak
call uncrustify -c base_config.cfg --no-backup %1
