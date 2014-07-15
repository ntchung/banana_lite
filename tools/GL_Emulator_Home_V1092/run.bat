set jar=X:\Casino-VP\#builds\MOTOROLA_V300\LasVegasVideoPoker_v0_2_2.jar
rem set jar=./NYNights_Motorola_Triplets_English_1_0_17.jar
rem set jar=./KingKong_MotorolaE1000_EN_108.jar
rem set jar=./AsphaltUrbanGT_LGCE500_EN_104.jar
rem set jar=./PhoneSpec.jar
rem set jar=./MMP_Motorola_i325_EN_001.jar
rem set jar=./And1Streetball_MOTOROLA_V300_EN_108.jar
set device=motorola_triplets

java -cp ./glemulator.jar;%jar%; emulator.GLEmulator ./glemulator.jar -device %device% -jar %jar%
pause