
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;

class Game implements CommandListener
{
	#include "stringdef.h"
	
	#include "Game_Key.h"
	
	// Notes
	// To add new game state:
	// + Add new state constants in Game_State_Constants.h
	// + Add new code file, ie. Game_State_Splash.h
	// + Code init, destroy, update, and paint for the state
	// + Include the code file here
	// + Add new switch in update(), initCurrentState(), destroyCurrentState() and paint()
	
	private Command backCommand;	
	
	public Game( AppCanvas canvas )
	{
		gameCanvas = canvas;
				
		Util.setRNGSeedFromSystemTime();
		gameTick = 0;		
		
		isGameRunning = true;
		isLoadingProcess = false;
		isCheatEnabled = false;
		cheatStep = 0;
		gameState = -1;	

		resetAllKeys();
		
		backCommand = new Command("Back", Command.BACK, 0);
        gameCanvas.addCommand(backCommand);
        gameCanvas.setCommandListener(this);
	}
	
	public void pauseGame()
	{		
		// free up resources		
	}
	
	public void resumeGame()
	{		
		resetAllKeys();
	}
	
	public void destroy()
	{
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
			//changeState( k_State_Splash );
		}
	}
	
	public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
            /*switch( gameState )
			{				
				
			}*/
        }
    }
	
	public void update()
	{		
		switch( gameState )
		{
			/*case k_State_Splash:
				updateSplash();
			break;

			case k_State_InGame:
				updateInGame();
			break;

			case k_State_MainMenu:
				updateMainMenu();
			break;*/
		}
		gameTick++;

		resetKeyState();

		if( gameState == 0x0FFF )
		{
			quitGame();
		}
		else
		{
			//updateSounds();			
		}
	}
	
	private void quitGame()
	{
		SoundPlayer.StopAllSounds();
		isGameRunning = false;
		//saveProfile();
	}
	
	private void initCurrentState()
	{
			
	}
	
	private void destroyCurrentState()
	{
			
	}
	
	public void paint( Graphics g )
	{
		currentGraphics = g;
		ASprites.currentGraphics = g;
		
		
		currentGraphics = null;	
	}	
	
	private void changeState( int state )
	{
		destroyCurrentState();
		gameState = state;
				
		resetAllKeys();
		initCurrentState();
	}
	
	public static int canvasWidth, canvasHeight;
	public static int halfCanvasWidth, halfCanvasHeight;
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
}
