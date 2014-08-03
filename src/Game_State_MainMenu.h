
private static final int MAIN_MENU_BASE = 0;
private static final int MAIN_MENU_HELP = 1;
private static final int MAIN_MENU_ABOUT = 2;

private Image imgTitle;
private GameMenu menuMain;

private int currentMainMenu;

private int showAdsCounter = 5;

private void initMainMenu()
{
	currentMainMenu = MAIN_MENU_BASE;
	imgTitle = Util.loadImage( "/title.png" );	
	
	if( imgWall == null )
	{
		imgWall = Util.loadImage( "/wall.png" );	
		wallHeight = imgWall.getHeight() - 16;
	}

	menuMain = new GameMenu( 5, GameMenu.kBarTypeShort );
	menuMain.setItem( 0, STR_NEWGAME );
	menuMain.setItem( 1, STR_SOUND_ON );
	menuMain.setItem( 2, STR_HELP );
	menuMain.setItem( 3, STR_ABOUT );
	menuMain.setItem( 4, STR_LEADERBOARD );
	menuMain.soundItem = 1;
	
	menuMain.init();
	
	setLeftSoftkey(kSoftkeyYes);
	setRightSoftkey(kSoftkeyQuit);
	
	if( showAdsCounter <= 0 )
	{
		showAdsCounter = 5;
		
		gameCanvas.midlet.showMidAds();		
	}
	--showAdsCounter;
}

private void destroyMainMenu()
{
	menuMain = null;	
	imgTitle = null;
}

private void updateMainMenu()
{
	if( currentMainMenu == MAIN_MENU_BASE )
	{
		setLeftSoftkey(kSoftkeyYes);
		setRightSoftkey(kSoftkeyQuit);
	
		if( updateMenu( menuMain ) )
		{	
			switch( menuMain.select )
			{
			case 0:
				changeState( k_State_InGame );		
			break;
			case 1:
				optionSound = optionSound == 0 ? 1 : 0;
			break;
			case 2:
				setLeftSoftkey(kSoftkeyYes);
				setRightSoftkey(kSoftkeyNone);
				currentMainMenu = MAIN_MENU_HELP;
			break;
			case 3:
				setLeftSoftkey(kSoftkeyYes);
				setRightSoftkey(kSoftkeyNone);
				currentMainMenu = MAIN_MENU_ABOUT;
			break;
			case 4:
				gameCanvas.midlet.switchToLeaderboard();
			break;
			}
			
			playSfx(SFX_MENU_HIT);
		}
		
		if( isRightSK )
		{
			quitGame();
		}
	}
	else if( currentMainMenu == MAIN_MENU_HELP || currentMainMenu == MAIN_MENU_ABOUT )
	{
		if( isLeftSK )
		{
			setLeftSoftkey(kSoftkeyYes);
			setRightSoftkey(kSoftkeyQuit);
			currentMainMenu = MAIN_MENU_BASE;
		}
	}
}

private void paintMainMenu()
{
	if( currentMainMenu == MAIN_MENU_BASE )
	{
		currentGraphics.setColor( 0x70c7ed );
		currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight - wallHeight );
	
		currentGraphics.drawImage( imgWall, 
			( canvasWidth - imgWall.getWidth() ) >> 1, 
			( canvasHeight - imgWall.getHeight() ), 0 );
			
		menuMain.paint( canvasWidth >> 1, canvasHeight / 4 );
		
		currentGraphics.drawImage( imgTitle, 0, 0, 0 );	
		
		int y = 2;
		font.drawString( STR_HIGHSCORE, (canvasWidth << 1) / 3, y, AFont.kAlignCenter );	
		font.drawNumber( highScore, (canvasWidth << 1) / 3, y + font.fontHeight, AFont.kAlignCenter );	
	}
	else if( currentMainMenu == MAIN_MENU_HELP )
	{
		currentGraphics.setColor( 0x002000 );
		currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight );
		
		currentGraphics.setColor( 0xffffff );
		currentGraphics.drawRect( 10, 10, canvasWidth - 20, canvasHeight - 20 );
		
		font.drawStringWrapCenter( STR_HELP_0, 20, 20, canvasWidth - 40 );	
	}
	else if( currentMainMenu == MAIN_MENU_ABOUT )
	{
		currentGraphics.setColor( 0x002000 );
		currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight );
		
		currentGraphics.setColor( 0xffffff );
		currentGraphics.drawRect( 10, 10, canvasWidth - 20, canvasHeight - 20 );
		
		font.drawStringWrapCenter( STR_ABOUT_0, 20, 20, canvasWidth - 40 );	
	}
}
