
private final static int kSplashLogo = 0;
private final static int kSplashLogoBegin = 1;
private final static int kSplashLogoWait = 2;
private final static int kSplashSelectLanguage = 3;
private final static int kSplashAskSound = 4;
private final static int kSplashWaitKey = 5;

int splashTick;
ASprites sprButtons;
int currentLanguage;
private Image logo;
private Image menuBG;
private long logoStartTime;
private int logoWaitFrame;

private GameMenu menuOptionSound;

private void initSplash()
{
	currentMenu = kSplashLogo;
	logo = Util.loadImage( "/splash.png" );	
	wasTouchDownInState = false;	
	
	menuOptionSound = new GameMenu( 2, GameMenu.kBarTypeShort );
	menuOptionSound.setItem( 0, STR_YES );
	menuOptionSound.setItem( 1, STR_NO );	
		
	unloadMainMenuAssets();
}

private void destroySplash()
{
	menuOptionSound = null;
	logo = null;
}

private void reloadLanguagePack()
{
	currentLanguage = optionLanguage;
	font.loadString( "/english.str" );
}

private final int resetMenuSelect( int i, int max )
{
	return i > max ? max : 0;
}

private void updateSplash()
{
	if( currentMenu == kSplashLogo )
	{			
		currentMenu = kSplashLogoBegin;
		logoStartTime = System.currentTimeMillis();
		logoWaitFrame = 0;
	}
	else if( currentMenu == kSplashLogoBegin )
	{		
		loadProfile();		
		
		// reverse here because of menu items order
		menuOptionSound.select = 1 - optionSound;		
	
		font = new AFont();
		font.loadFont( "/font.png", "/font.dat" );
		
		try		
		{
			font.createVersionNumber( versionString );			
		}
		catch( Exception ex )
		{
		}
		
		currentLanguage = -1;	
		
		loadAPSfx();
		reloadLanguagePack();
		
		sprButtons = new ASprites();
		sprButtons.loadSprite( "/buttons.png", "/buttons.dat" );	
		
		GameMenu.font = font;
		GameMenu.sprButtons = sprButtons;
		initSoftkeys();
				
		loadMainMenuAssets();
		splashTick = 0;
		
		currentMenu = kSplashLogoWait;
	}
	else if( currentMenu == kSplashLogoWait )
	{
#ifdef QUICK_START_GAME

		optionSound = 0;
		changeState( k_State_InGame );
		
#else
	
		logoWaitFrame++;

		final int waitTime = 2000;
		
		if( System.currentTimeMillis() - logoStartTime > waitTime || logoWaitFrame > waitTime )
		{
			currentMenu = kSplashAskSound;
			menuOptionSound.init();
		}
#endif
	}
	else if( currentMenu == kSplashAskSound )
	{		
		if( updateMenu( menuOptionSound ) )
		{
			optionSound = 1 - menuOptionSound.select;		
			playMainMenuBGM();						
			
			saveProfile();
			resetSoftkeys();
			
			changeState( k_State_MainMenu );			
		}
	}	
	
	wasTouchDownInState = isTouchDown;
}

private void paintSplash()
{	
	if( currentMenu == kSplashLogo || currentMenu == kSplashLogoBegin || currentMenu == kSplashLogoWait )
	{		
		if( currentGraphics != null && logo != null )
		{					
			currentGraphics.drawImage( logo, 
				( canvasWidth - logo.getWidth() ) >> 1, 
				( canvasHeight - logo.getHeight() ) >> 1, 
				0 );
		}
	}
	else if( currentMenu == kSplashAskSound )
	{
		currentGraphics.setColor( 0xFFFFFF );
		currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight );
		
		font.drawStringWrapCenter( STR_ASK_SOUND, 0, canvasHeight / 3, canvasWidth );
		
		menuOptionSound.paint( canvasWidth >> 1, canvasHeight >> 1 );
	}	
}

private final boolean updateMenu( GameMenu menu )
{
	if( menu.update( isKeyMenuConfirm(), isKeyMenuPrior(), isKeyMenuNext(), isTouchDown, touchX, touchY ) )
	{
		if( menu.barType != GameMenu.kBarTypeTiny )
		{
			playSfx( SFX_MENU_HIT );
		}
		return true;
	}
	
	return false;
}

private final void loadMainMenuAssets()
{
}

private final void unloadMainMenuAssets()
{

}
