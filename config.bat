@echo off
rem Base paths
set JAVA_HOME=D:\j2sdk1.4.2_19
set WTK_HOME=D:\WTK22

set RAW_DIR=%CD%\data\sprites
set DATA_TEMP=%CD%\_temp
set HEADER_DIR=%CD%\src

rem Extend paths
set JAVA_BIN=%JAVA_HOME%\bin
set WTK_BIN=%WTK_HOME%\bin
set EMULATOR_BIN=tools\GL_Emulator_Home_V1092
set CPP_BIN=tools\cpp\cpp.exe

rem Config
set GAME_NAME=CastleAttack

rem string
set STRING_TOOL=%CD%\tools\FontGenerator.exe
set STRING_DIR=%CD%\data\string
set FONT_DIR=%CD%\data\font