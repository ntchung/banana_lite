
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;

#include "Buttons.h"
#include "KnightAnim.h"
#include "MeleeAnim.h"
#include "FlyerAnim.h"
#include "RangedAnim.h"
#include "ProjectilesAnim.h"

class Game implements CommandListener
{
	#include "stringdef.h"
	
	#include "Game_Key.h"
	#include "Game_Profile.h"
	#include "Game_Sound.h"
	#include "Game_Softkeys.h"
	
	// Notes
	// To add new game state:
	// + Add new state constants in Game_State_Constants.h
	// + Add new code file, ie. Game_State_Splash.h
	// + Code init, destroy, update, and paint for the state
	// + Include the code file here
	// + Add new switch in update(), initCurrentState(), destroyCurrentState() and paint()
	#include "Game_State_Constants.h"
	#include "Game_State_Splash.h"
	#include "Game_State_MainMenu.h"
	#include "Game_State_InGame.h"
	
	private Command backCommand;	
	
	public static Game Instance;
	
	public Game( AppCanvas canvas )
	{
		Instance = this;
		gameCanvas = canvas;
				
		setSize( 240, 320 );
		
		SoundPlayer.Init( SOUND_NUM, NUM_SOUND_CHANNELS );		

		Util.setRNGSeedFromSystemTime();
		gameTick = 0;		
		
		isGameRunning = true;
		isLoadingProcess = false;
		isCheatEnabled = false;
		cheatStep = 0;
		gameState = -1;	

		resetAllKeys();
		
		playerCharacter = new PlayerCharacter();
		
		enemiesManager = new EnemiesManager();
		projectilesManager = new ProjectilesManager();
				
		if( hasTouch )
		{
			backCommand = new Command("Back", Command.BACK, 0);
			gameCanvas.addCommand(backCommand);
			gameCanvas.setCommandListener(this);
		}		
	}
	
	public void pauseGame()
	{		
		// free up resources
		unloadAllSounds();
		
		unloadMainMenuAssets();
	}
	
	public void resumeGame()
	{
		loadMainMenuAssets();
		
		resetAllKeys();
	}
	
	public void destroy()
	{
		unloadAPSfx();
		SoundPlayer.Quit();
	}
	
	public void setSize( int w, int h )
	{		
		canvasWidth = w;
		canvasHeight = h;
		halfCanvasWidth = canvasWidth >> 1;
		halfCanvasHeight = canvasHeight >> 1;
		
		ASprites.setSpriteClip( 0, 0, canvasWidth, canvasHeight );
			
		if( gameState == -1 )
		{
			changeState( k_State_Splash );
		}
	}
	
	public void commandAction(Command c, Displayable d) {
		resetAllKeys();
        if (c == backCommand) {
            switch( gameState )
			{				
				case k_State_Splash:
					quitGame();
				break;
				
				case k_State_MainMenu:
					if( currentMainMenu == MAIN_MENU_BASE )
					{
						quitGame();
					}
					else
					{
						setLeftSoftkey(kSoftkeyYes);
						setRightSoftkey(kSoftkeyQuit);
						
						currentMainMenu = MAIN_MENU_BASE;
						menuMain.init();
					}
				break;

				case k_State_InGame:
					saveProfile();
					saveGameData();
					changeState( k_State_MainMenu );
				break;
			}
        }
    }
	
	public void update()
	{		
		updateSoftkeys();
		
		switch( gameState )
		{
			case k_State_Splash:
				updateSplash();
			break;

			case k_State_InGame:
				updateInGame();
			break;

			case k_State_MainMenu:
				updateMainMenu();
			break;
		}
		gameTick++;

		resetKeyState();

		if( gameState == 0x0FFF )
		{
			quitGame();
		}
		else
		{
			updateSounds();			
		}
	}
	
	private void quitGame()
	{
		SoundPlayer.StopAllSounds();
		isGameRunning = false;
	}
	
	private void initCurrentState()
	{
		switch( gameState )
		{
			case k_State_Splash:
				initSplash();
			break;

			case k_State_InGame:
				initInGame();
			break;

			case k_State_MainMenu:
				initMainMenu();
			break;
		}		
	}
	
	private void destroyCurrentState()
	{
		switch( gameState )
		{
			case k_State_Splash:
				destroySplash();
			break;

			case k_State_InGame:
				destroyInGame();
			break;

			case k_State_MainMenu:
				destroyMainMenu();
			break;
		}		
	}
	
	public void paint( Graphics g )
	{
		currentGraphics = g;
		ASprites.currentGraphics = g;
		
		switch( gameState )
		{
			case k_State_Splash:
				paintSplash();
			break;

			case k_State_InGame:
				paintInGame();
			break;

			case k_State_MainMenu:
				paintMainMenu();
			break;
		}
		
		drawSoftkeys();

		currentGraphics = null;	
	}	
	
	private void changeState( int state )
	{
		destroyCurrentState();
		gameState = state;
				
		resetAllKeys();
		resetSoftkeys();
		initCurrentState();
	}
	
	public static int canvasWidth, canvasHeight;
	public static int halfCanvasWidth, halfCanvasHeight;
	public static int wallHeight;
	public static boolean isCheatEnabled;	
	
	public static AFont font;
	
	private int gameState;
	private int gameTick;
	private Graphics currentGraphics;
	public boolean isGameRunning;
	public static boolean isLoadingProcess;
		
	public static String versionString;
	private AppCanvas gameCanvas;
	
	public static boolean hasTouch;	
	private static boolean wasTouchDownInState;
	private int currentMenu;
	private int cheatStep;	
		
	private Image imgWall;
	
	private PlayerCharacter playerCharacter;
	private EnemiesManager enemiesManager;
	private ProjectilesManager projectilesManager;
}
