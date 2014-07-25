@echo off

SETLOCAL EnableDelayedExpansion


call config.bat

md _tempsrc
md _release
md _temp

del /s /q _tempsrc\*.*
del /s /q _temp\*.class
del /s /q _release\*.jar

cls

echo Preprocessing
for %%f in (src\*.java) do %CPP_BIN% -P -I src %%f _tempsrc\%%~nf.java

echo Compiling

rem %JAVA_BIN%\javac -target 1.1 -bootclasspath %WTK_HOME%\lib\cldcapi11.jar;%WTK_HOME%\lib\midpapi20.jar -classpath %NOKIA_SDK_HOME%\lib\nokiaui.jar _tempsrc\*.java -d _temp
%JAVA_BIN%\javac -target 1.4 -source 1.4 -classpath %NOKIA_SDK_HOME%\lib\nokiaui.jar;tools\VservAdEngine.jar -bootclasspath %WTK_HOME%\lib\cldcapi11.jar;%WTK_HOME%\lib\midpapi20.jar _tempsrc\*.java -d _temp

echo Preverifying
xcopy /S /Y tools\VservAdEngine _temp
%WTK_BIN%\preverify1.1 -classpath %WTK_HOME%\lib\cldcapi10.jar;%WTK_HOME%\lib\midpapi20.jar;tools\jsr120.jar -d _temp _temp

echo Jaring
%JAVA_BIN%\jar cfm _release\%GAME_NAME%.jar MANIFEST.MF -C _temp .

ENDLOCAL
