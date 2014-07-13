@echo off

SETLOCAL EnableDelayedExpansion


call config.bat
call java -jar tools\proguard.jar @midlets.pro

del _release\%GAME_NAME%.jar
ren _release\%GAME_NAME%_release.jar %GAME_NAME%.jar

ENDLOCAL
