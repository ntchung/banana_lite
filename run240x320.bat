call config.bat

java.exe -cp ./%EMULATOR_BIN%/glemulator.jar -Dmicroedition.locale=en -Dvk_pound=VK_DIVIDE  -Dvk_fire=VK_ENTER  -Dvk_star=VK_MULTIPLY  -Dvk_rsk=VK_F2  -Dvk_lsk=VK_F1   emulator.GLEmulator  -device "nokia_midp2" -dSCREEN_HEIGHT=320 -dSCREEN_WIDTH=240 -jar "_release\%GAME_NAME%.jar" 

rem del prefs.txt
del *.log
