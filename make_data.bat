call config

md %DATA_TEMP%
 
del /s /q %DATA_TEMP%\*.*
 
cls 

copy data\sounds\*.wav %DATA_TEMP%

cd %RAW_DIR%
call MakeSprite.exe buttons.sprite buttons.dat -jf Buttons.h
call MakeSprite.exe knight.sprite knight.dat -ja KnightAnim.h
call MakeSprite.exe goblin.sprite goblin.dat -ja MeleeAnim.h
call MakeSprite.exe projectiles.sprite projectiles.dat -ja ProjectilesAnim.h

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
