call config

md %DATA_TEMP%
 
del /s /q %DATA_TEMP%\*.*
 
cls 

cd %RAW_DIR%
rem call MakeSprite.exe buttons.sprite buttons.dat -jf Buttons.h -ja ButtonsAnim.h
rem call MakeSprite.exe boardgrid.sprite boardgrid.dat -jf BoardGrid.h
rem call MakeSprite.exe pieces1.sprite pieces1.dat

move *.h %HEADER_DIR%
cd..\..
copy data\sprites\*.png %DATA_TEMP%
copy data\sprites\*.dat %DATA_TEMP%

rem make string
cd %STRING_DIR%
call %STRING_TOOL% %FONT_DIR%\VN_Verdana.fnt %STRING_DIR%\english.txt
copy string.pack english.str
del string.pack
move *.h %HEADER_DIR%
move *.str %DATA_TEMP%

cd %FONT_DIR%
call MakeSprite.exe font.sprite font.dat
move *.dat %DATA_TEMP%
copy *.png %DATA_TEMP%

pause
